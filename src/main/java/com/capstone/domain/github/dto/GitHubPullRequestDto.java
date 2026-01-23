package com.capstone.domain.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class GitHubPullRequestDto {
    private Long id;
    private String title;
    private String state;
    private String body;

    @JsonProperty("html_url")
    private String url;

    private int number;

    @JsonProperty("created_at")
    private String createdAt;

    private User user;

    private Assignee assignee;

    private Head head;
    private Base base;

    private List<Label> labels;

    private Integer additions;
    private Integer deletions;

    @JsonProperty("changed_files")
    private Integer changedFiles;

    private Integer comments;

    // 리뷰 정보
    private List<Review> reviews;

    // CI/CD 상태 (Check runs 등)
    private List<Check> checks;

    // 내부 객체 정의들
    @Getter
    public static class User {
        private String login;

        @JsonProperty("avatar_url")
        private String avatarUrl;
    }

    @Getter
    public static class Assignee {
        private String login;

        @JsonProperty("avatar_url")
        private String avatarUrl;
    }

    @Getter
    public static class Head {
        private String ref;
    }

    @Getter
    public static class Base {
        private String ref;
    }

    @Getter
    public static class Label {
        private Long id;
        private String name;
        private String color;
    }

    @Getter
    public static class Review {
        private Long id;
        private String state;
        private User user;
    }

    @Getter
    public static class Check {
        private Long id;
        private String name;
        private String status;
    }
}