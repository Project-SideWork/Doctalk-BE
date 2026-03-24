package com.capstone.domain.project.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProjectSaveRequest(
        @Nullable
        String projectId,
        @NotBlank
        @Size(min = 1)
        String projectName,
        @NotBlank
        @Size(min = 1)
        String description,
        @Nullable
        List<String> invitedEmails,
        @Nullable
        @Valid
        List<ProjectGithubInfo> githubInfos
){}
