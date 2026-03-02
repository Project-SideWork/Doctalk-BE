package com.capstone.domain.project.entity;

import lombok.*;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class ProjectGithubPullRequest {

    private Long id;
    private Long repositoryId;
    private Integer prNumber;
    private String prTitle;
    private String prState;
    private String prAuthor;

    private Instant prCreatedAt;
    private Instant prClosedAt;
    private Instant prMergedAt;

    private Boolean merged;

    private Integer commits;
    private Integer additions;
    private Integer deletions;
    private Integer changedFiles;

    private Integer commentCount;
    private Integer reviewCommentCount;

    public static ProjectGithubPullRequest create(
            Long id,
            Long repositoryId,
            Integer prNumber,
            String prTitle,
            String prState,
            String prAuthor,
            Instant createdAt,
            Instant closedAt,
            Instant mergedAt,
            Boolean merged,
            Integer commits,
            Integer additions,
            Integer deletions,
            Integer changedFiles,
            Integer commentCount,
            Integer reviewCommentCount
    ) {
        return ProjectGithubPullRequest.builder()
                .id(id)
                .repositoryId(repositoryId)
                .prNumber(prNumber)
                .prTitle(prTitle)
                .prState(prState)
                .prAuthor(prAuthor)
                .prCreatedAt(createdAt)
                .prClosedAt(closedAt)
                .prMergedAt(mergedAt)
                .merged(merged)
                .commits(commits)
                .additions(additions)
                .deletions(deletions)
                .changedFiles(changedFiles)
                .commentCount(commentCount)
                .reviewCommentCount(reviewCommentCount)
                .build();
    }
}
