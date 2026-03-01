package com.capstone.domain.project.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ProjectOrganization {
    private String orgName;
    private List<String> orgRepos;
    private List<ProjectGithubIssue> githubIssues = new ArrayList<>();

    public static ProjectOrganization create(String orgName, List<String> orgRepos) {
        return ProjectOrganization.builder()
                .orgName(orgName)
                .orgRepos(orgRepos)
                .build();
    }

    public void addGithubIssue(ProjectGithubIssue issue) {
        this.getGithubIssues().add(issue);
    }
}