package com.capstone.domain.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubIssueWebhookDto {

    private String action;

    private Installation installation;

    private Repository repository;

    private Issue issue;

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
    public static class Issue {
        private Long id;
        private Integer number;
        private String title;
        private String state;

        @JsonProperty("created_at")
        private OffsetDateTime createdAt;

        @JsonProperty("closed_at")
        private OffsetDateTime closedAt;

        private User user;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String login;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Organization {
        private String login;
    }
}
