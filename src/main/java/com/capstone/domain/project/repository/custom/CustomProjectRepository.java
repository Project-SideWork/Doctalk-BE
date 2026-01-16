package com.capstone.domain.project.repository.custom;

import com.capstone.domain.project.dto.request.ProjectAuthorityRequest;
import com.capstone.domain.project.entity.Project;

import java.util.List;

public interface CustomProjectRepository {
    Project findByProjectName(String projectName);
    List<Project> findAllById(List<String> ids);
    void addTaskIdAtomically(String projectId, String taskId);
}
