package com.capstone.domain.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 응답에 필요 없는 필드는 무시
public class GitHubOrgEventDto {
    private String id;
    private String type;
    private Actor actor;
    private Repo repo;
    private Payload payload;

    @JsonProperty("public")
    private boolean isPublic;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Actor {
        private Long id;
        private String login;

        @JsonProperty("display_login")
        private String displayLogin;

        @JsonProperty("avatar_url")
        private String avatarUrl;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repo {
        private Long id;
        private String name;
        private String url;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {
        private String action;

        @JsonProperty("pull_request")
        private PullRequest pullRequest;

        @JsonProperty("issue")
        private Issue issue;

        @JsonProperty("comment")
        private Comment comment;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Issue {
        private Long id;
        private int number;
        private String title;
        private String state;

        @JsonProperty("html_url")
        private String htmlUrl;

        private GitHubUser user;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Comment {
        private String body;

        @JsonProperty("html_url")
        private String htmlUrl;

        private GitHubUser user;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PullRequest {
        private Long id;
        private Integer number;
        private String title;

        @JsonProperty("url")
        private String htmlUrl;

        private String state;

        @JsonProperty("user")
        private GitHubUser user;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubUser {
        private String login;

        @JsonProperty("avatar_url")
        private String avatarUrl;

        @JsonProperty("html_url")
        private String htmlUrl;
    }
}
