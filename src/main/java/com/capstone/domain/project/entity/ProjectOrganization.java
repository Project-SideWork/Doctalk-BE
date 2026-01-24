package com.capstone.domain.project.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectOrganization {
    private String orgName;
    private List<String> projectRepos;

    public static ProjectOrganization create(String orgName, List<String> projectRepos) {
        return new ProjectOrganization(orgName, projectRepos);
    }
}