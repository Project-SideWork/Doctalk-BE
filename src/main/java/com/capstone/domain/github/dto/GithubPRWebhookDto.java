package com.capstone.domain.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubPRWebhookDto {

    private String action;

    private Installation installation;

    private Repository repository;

    @JsonProperty("pull_request")
    private PullRequest pullRequest;

    private Organization organization;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Installation {
        private Long id;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repository {
        private Long id;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PullRequest {

        private Long id;

        private Integer number;

        private String title;

        private String state;

        private Boolean merged;

        private Integer commits;

        private Integer additions;

        private Integer deletions;

        @JsonProperty("changed_files")
        private Integer changedFiles;

        private Integer comments;

        @JsonProperty("review_comments")
        private Integer reviewComments;

        @JsonProperty("created_at")
        private OffsetDateTime createdAt;

        @JsonProperty("closed_at")
        private OffsetDateTime closedAt;

        @JsonProperty("merged_at")
        private OffsetDateTime mergedAt;

        private User user;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private Long id;
        private String login;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Organization {
        private String login;
    }
}