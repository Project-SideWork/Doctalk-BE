package com.capstone.domain.project.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProjectSaveRequest(
        @Nullable
        String projectId,
        @NotNull
        @Size(min = 1)
        String projectName,
        @NotNull
        @Size(min = 1)
        String description,
        @Nullable
        List<String> invitedEmails,
        List<ProjectGithubInfo> githubInfos
){}
