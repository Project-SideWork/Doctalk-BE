package com.capstone.domain.user.repository.custom;

import com.capstone.domain.project.dto.request.ProjectAuthorityRequest;
import com.capstone.domain.project.entity.Project;
import com.capstone.domain.user.entity.ProjectUser;

import java.util.List;
import java.util.Optional;

public interface CustomProjectUserRepository {
    List<String> findUserIdByProjectId(String projectId);
    List<Project> findProjectsByUserId(String userId);
    List<ProjectUser> findUserIdAndRoleByProjectId(String projectId);
    List<ProjectUser> findUserIdAndRoleByProjectIdIn(List<String> projectIds);
    boolean existsByProjectIdAndUserId(String projectId, String userId);
}
