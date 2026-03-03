package com.capstone.domain.project.entity;


import lombok.*;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class ProjectGithubPullRequestReview {
    private Long id;
    private Long repositoryId;
    private Long pullRequestId;
    private Integer pullRequestNumber;

    private Long reviewerId;
    private String reviewer;
    private String state;
    private String body;

    private Instant submittedAt;

    public static ProjectGithubPullRequestReview create(
            Long id,
            Long repositoryId,
            Long pullRequestId,
            Integer pullRequestNumber,
            Long reviewerId,
            String reviewer,
            String state,
            String body,
            Instant submittedAt
    ) {
        return ProjectGithubPullRequestReview.builder()
                .id(id)
                .repositoryId(repositoryId)
                .pullRequestId(pullRequestId)
                .pullRequestNumber(pullRequestNumber)
                .reviewerId(reviewerId)
                .reviewer(reviewer)
                .state(state)
                .body(body)
                .submittedAt(submittedAt)
                .build();
    }
}
