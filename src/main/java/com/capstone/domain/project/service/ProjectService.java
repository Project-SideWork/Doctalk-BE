package com.capstone.domain.project.service;

import com.capstone.domain.file.service.FileService;
import com.capstone.domain.project.dto.request.ProjectUpdateRequest;
import com.capstone.domain.project.dto.response.ProjectCoworkerDto;
import com.capstone.domain.project.dto.response.ProjectResponse;
import com.capstone.domain.project.dto.request.ProjectSaveRequest;
import com.capstone.domain.project.entity.Project;
import com.capstone.domain.project.repository.ProjectRepository;

import com.capstone.domain.user.entity.ProjectUser;
import com.capstone.domain.user.entity.User;
import com.capstone.domain.user.exception.InvalidUserException;
import com.capstone.domain.user.repository.ProjectUserRepository;
import com.capstone.domain.user.repository.UserRepository;
import com.capstone.domain.user.service.UserService;

import com.capstone.global.kafka.dto.ProjectChangePayload;
import com.capstone.global.kafka.dto.detail.ProjectChangeDetail;
import com.capstone.global.kafka.service.KafkaProducerService;
import com.capstone.global.kafka.topic.KafkaEventTopic;
import com.capstone.global.response.exception.GlobalException;
import com.capstone.global.response.status.ErrorStatus;
import com.capstone.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;
    private final ProjectUserRepository projectUserRepository;
    private final FileService fileService;


    @Transactional
    public Project saveProject(CustomUserDetails customUserDetails, ProjectSaveRequest projectSaveRequest){
        Project project = projectRepository.save(projectSaveRequest.toProject());
        saveProjectUsers(projectSaveRequest, project, customUserDetails.getEmail());
        return project;
    }


    public ProjectResponse getProjectContent(String projectId){
        List<ProjectUser> projectUserList= projectUserRepository.findUserIdAndRoleByProjectId(projectId);

        List<String> allUserEmails = projectUserList.stream()
            .map(ProjectUser::getUserId)
            .distinct()
            .toList();

        Map<String, User> userMap = userRepository.findAllByEmailIn(allUserEmails)
            .stream()
            .collect(Collectors.toMap(User::getEmail, user -> user));

        List<ProjectCoworkerDto> projectCoworkerDtos= projectUserList.stream()
            .map(coworker->
                {
                    User user = userMap.get(coworker.getUserId());
                    String userName = user != null ? user.getName() : coworker.getUserId();
                    return ProjectCoworkerDto.from(
                        coworker.getUserId(),
                        coworker.getRole(),
                        userName);
                }
            )
            .toList();
        return ProjectResponse.from(findProjectByProjectIdOrThrow(projectId),projectCoworkerDtos);
    }

    public Project processRegister(CustomUserDetails customUserDetails, ProjectSaveRequest projectSaveRequest){
        Project project = saveProject(customUserDetails, projectSaveRequest);
        if (projectSaveRequest.invitedEmails() != null) {
            userService.participateProcess(projectSaveRequest.invitedEmails(), project.getId());
        }
        kafkaProducerService.sendEvent(KafkaEventTopic.PROJECT_CREATED, ProjectChangePayload.from(project, null, null, customUserDetails.getEmail(), projectSaveRequest.invitedEmails()));
        return project;
    }



    @Transactional
    public Project processUpdate(String projectId, ProjectUpdateRequest projectUpdateRequest, CustomUserDetails customUserDetails){
        Project project = findProjectByProjectIdOrThrow(projectId);
        ProjectChangeDetail beforeUpdate = ProjectChangeDetail.from(project);

        if(projectUpdateRequest.imageId() != null){
            String oldImageId = project.getImageId();
            if (oldImageId != null && !oldImageId.equals(projectUpdateRequest.imageId())) {
                fileService.delete(customUserDetails, oldImageId);
            }
            project.updateProjectInfoWithImage(projectUpdateRequest.projectName(), projectUpdateRequest.description(),projectUpdateRequest.imageId());
        }
        else {
            project.updateProjectInfo(projectUpdateRequest.projectName(), projectUpdateRequest.description());
        }


        ProjectChangeDetail afterUpdate = ProjectChangeDetail.from(project);

        List<String> coworkers = projectUserRepository.findUserIdByProjectId(project.getId());

        kafkaProducerService.sendEvent(KafkaEventTopic.PROJECT_UPDATED, ProjectChangePayload.from(project, beforeUpdate, afterUpdate, customUserDetails.getEmail(), coworkers));

        projectRepository.save(project);
        return project;
    }

    public Project findProjectByProjectIdOrThrow(String projectId){
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new GlobalException(ErrorStatus.PROJECT_NOT_FOUND));
    }


    public List<ProjectResponse> getProjectList(CustomUserDetails customUserDetails) {
        List<String> projectIds = parseProjectIds(projectUserRepository.findByUserId(customUserDetails.getEmail()));
        List<Project> projects = projectRepository.findAllById(projectIds);

        if (projects.isEmpty()) {
            return List.of();
        }

        List<String> projectIdList = projects.stream()
            .map(Project::getId)
            .toList();
        List<ProjectUser> allProjectUsers = projectUserRepository.findUserIdAndRoleByProjectIdIn(projectIdList);

        Map<String, List<ProjectUser>> projectUserMap = allProjectUsers.stream()
            .collect(Collectors.groupingBy(ProjectUser::getProjectId));

        List<String> allUserEmails = allProjectUsers.stream()
            .map(ProjectUser::getUserId)
            .distinct()
            .toList();
        Map<String, User> userMap = userRepository.findAllByEmailIn(allUserEmails)
            .stream()
            .collect(Collectors.toMap(User::getEmail, user -> user));

        return projects.stream()
            .map(project -> {
                List<ProjectUser> projectUserList = projectUserMap.getOrDefault(
                    project.getId(),
                    List.of()
                );

                List<ProjectCoworkerDto> projectCoworkerDtos = projectUserList.stream()
                    .map(coworker -> {
                        User user = userMap.get(coworker.getUserId());
                        String userName = user != null ? user.getName() : coworker.getUserId();

                        return ProjectCoworkerDto.from(
                            coworker.getUserId(),
                            coworker.getRole(),
                            userName);

                    })
                    .toList();
                return ProjectResponse.from(project, projectCoworkerDtos);
            })
            .toList();
    }

    public List<String> parseProjectIds(List<ProjectUser> projectUsers){
        return projectUsers.stream()
            .map(ProjectUser::getProjectId)
            .toList();
    }

    public void saveProjectUsers(ProjectSaveRequest projectSaveRequest, Project project, String inviterEmail){
        List<ProjectUser> projectUsers = new ArrayList<>();
        List<String> invalidEmails = new ArrayList<>();

        if (projectSaveRequest.invitedEmails() != null && !projectSaveRequest.invitedEmails().isEmpty()) {
            projectSaveRequest.invitedEmails().forEach(
                invitedEmail -> {
                    if (userRepository.findUserByEmail(invitedEmail).isEmpty()){
                        invalidEmails.add(invitedEmail);
                    }
                }
            );
            projectUsers.addAll(
                projectSaveRequest.invitedEmails().stream()
                    .map(email -> ProjectUser.builder()
                        .projectId(project.getId())
                        .userId(email)
                        .role("ROLE_MEMBER")
                        .joinedAt(LocalDate.now().toString())
                        .build()
                    ).toList());
        }
        if (!invalidEmails.isEmpty()) {
            throw new InvalidUserException(invalidEmails);
        }
        ProjectUser inviteUser = ProjectUser.builder()
            .projectId(project.getId())
            .userId(inviterEmail)
            .role("ROLE_MANAGER")
            .joinedAt(LocalDate.now().toString())
            .build();
        projectUsers.add(inviteUser);
        projectUserRepository.saveAll(projectUsers);


    }
}
