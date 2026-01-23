package com.capstone.domain.github.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class GitHubReviewCommentDto {
    private String url;
    private long pull_request_review_id;
    private long id;
    private String node_id;
    private String diff_hunk;
    private String path;
    private int position;
    private int original_position;
    private String commit_id;
    private String original_commit_id;
    private Long in_reply_to_id; // nullable

    private User user;
    private String body;
    private ZonedDateTime created_at;
    private ZonedDateTime updated_at;
    private String html_url;
    private String pull_request_url;
    private String author_association;

    private Links _links;

    @Data
    public static class User {
        private String login;
        private long id;
        private String node_id;
        private String avatar_url;
        private String gravatar_id;
        private String url;
        private String html_url;
        private String followers_url;
        private String following_url;
        private String gists_url;
        private String starred_url;
        private String subscriptions_url;
        private String organizations_url;
        private String repos_url;
        private String events_url;
        private String received_events_url;
        private String type;
        private boolean site_admin;
    }

    @Data
    public static class Links {
        private Link self;
        private Link html;
        private Link pull_request;

        @Data
        public static class Link {
            private String href;
        }
    }
}
