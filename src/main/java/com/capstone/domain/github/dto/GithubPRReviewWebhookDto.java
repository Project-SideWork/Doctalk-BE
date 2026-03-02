package com.capstone.domain.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubPRReviewWebhookDto {

    private String action;

    private Installation installation;
    private Repository repository;
    private Organization organization;

    private Review review;

    @JsonProperty("pull_request")
    private PullRequest pullRequest;


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
    public static class Organization {
        private String login;
    }


    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Review {

        private Long id;

        private String state;

        private String body;

        @JsonProperty("submitted_at")
        private OffsetDateTime submittedAt;

        private User user;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PullRequest {

        private Long id;

        private Integer number;

        private String title;

        private String state;

        @JsonProperty("closed_at")
        private OffsetDateTime closedAt;

        @JsonProperty("merged_at")
        private OffsetDateTime mergedAt;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String login;
    }
}
