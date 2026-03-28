package com.capstone.global.security;

import com.capstone.domain.document.entity.Document;
import com.capstone.domain.document.repository.DocumentRepository;
import com.capstone.domain.project.repository.ProjectRepository;
import com.capstone.domain.task.entity.Task;
import com.capstone.domain.task.repository.TaskRepository;
import com.capstone.domain.user.repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectAuthorityEvaluator {
    private final ProjectUserRepository projectUserRepository;
    private final DocumentRepository documentRepository;
    private final TaskRepository taskRepository;


    public boolean hasPermission(String projectId, List<String> requiredRoles, Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return projectUserRepository.findByProjectIdAndUserId(projectId, user.getEmail())
                .map(member -> requiredRoles.stream()
                        .anyMatch(role -> role.equalsIgnoreCase(member.getRole())))
                .orElse(false);
    }

    public boolean hasTaskPermission(String taskId, List<String> requiredRoles, Authentication authentication) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        return hasPermission(task.getProjectId(), requiredRoles, authentication);
    }

    public boolean hasDocumentPermission(String documentId, List<String> requiredRoles, Authentication authentication) {
        Document document = documentRepository.findById(documentId).orElseThrow();
        return hasPermission(document.getProjectId(), requiredRoles, authentication);
    }
}