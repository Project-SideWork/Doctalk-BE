package com.capstone.domain.project.entity;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectGithubIssue {
    private Long id;
    private Long repositoryId;
    private Integer issueNumber;
    private String issueTitle;
    private String issueState;
    private String issueOpenUser;
    private Instant issueCreatedAt;
    private Instant issueClosedAt;

    public static ProjectGithubIssue create(Long id, Long repositoryId,
                                            Integer issueNumber, String issueTitle,
                                            String issueState, String issueOpenUser,
                                            OffsetDateTime issueCreatedAt) {
        return new ProjectGithubIssue(
                id, repositoryId, issueNumber, issueTitle, issueState, issueOpenUser,
                issueCreatedAt.toInstant(), null
        );
    }
}
