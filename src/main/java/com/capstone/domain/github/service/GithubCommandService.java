package com.capstone.domain.github.service;

import com.capstone.domain.github.dto.GithubIssueWebhookDto;
import com.capstone.domain.project.entity.Project;
import com.capstone.domain.project.entity.ProjectGithubIssue;
import com.capstone.domain.project.entity.ProjectOrganization;
import com.capstone.domain.project.repository.ProjectRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubCommandService {
    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper;


    public void createIssue(String payload) throws JsonProcessingException {
        GithubIssueWebhookDto dto = objectMapper.readValue(payload, GithubIssueWebhookDto.class);
        String orgName = dto.getOrganization().getLogin();
        Project project = projectRepository.findByGithubOrganization(orgName);
        ProjectOrganization organization = project.getProjectOrganizations()
                .stream()
                .filter(org -> org.getOrgName().equals(orgName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        ProjectGithubIssue projectGithubIssue = ProjectGithubIssue.create(
                dto.getIssue().getId(), dto.getRepository().getId(),
                dto.getIssue().getNumber(), dto.getIssue().getTitle(),
                dto.getIssue().getState(), dto.getIssue().getUser().getLogin(),
                dto.getIssue().getCreatedAt()
        );

        organization.addGithubIssue(projectGithubIssue);

        projectRepository.save(project);
    }

}
