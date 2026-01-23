package com.capstone.domain.project.dto.request;

import java.util.List;

public record ProjectGithubInfo(
        String githubOrgName,
        List<String> orgRepos
){}
