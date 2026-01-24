package com.capstone.domain.github.dto;


import com.capstone.domain.user.entity.User;
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

        private String url;
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
        @JsonProperty("push_id")
        private Long pushId;
        private String ref;
        private String head;
        private List<Commit> commits;

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

        private User user;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Comment {
        private Long id;
        private String body;

        @JsonProperty("html_url")
        private String htmlUrl;

        private User user;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Commit {
        private String sha;
        private Author author;
        private String message;
        private String url;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        private String name;
        private String email;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PullRequest {
        private Long id;
        private String url;
        private String title;
        private String state;
    }
}
