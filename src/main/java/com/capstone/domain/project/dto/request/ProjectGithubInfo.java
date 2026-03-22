package com.capstone.domain.project.dto.request;

import jakarta.annotation.Nullable;

import java.util.List;

public record ProjectGithubInfo(
        @Nullable
        String githubOrgName,
        @Nullable
        List<String> orgRepos
){}
