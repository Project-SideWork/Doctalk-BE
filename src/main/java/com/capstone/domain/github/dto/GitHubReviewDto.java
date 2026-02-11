package com.capstone.domain.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitHubReviewDto {
    private Long id;

    private User user;

    private String body;
    private String state;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("pull_request_url")
    private String pullRequestUrl;

    private Links _links;

    @JsonProperty("submitted_at")
    private String submittedAt;

    @JsonProperty("commit_id")
    private String commitId;

    @JsonProperty("author_association")
    private String authorAssociation;

    @Data
    public static class User {
        private String login;
        private Long id;

        @JsonProperty("node_id")
        private String nodeId;

        @JsonProperty("avatar_url")
        private String avatarUrl;

        @JsonProperty("gravatar_id")
        private String gravatarId;

        private String url;

        @JsonProperty("html_url")
        private String htmlUrl;

        @JsonProperty("followers_url")
        private String followersUrl;

        @JsonProperty("following_url")
        private String followingUrl;

        @JsonProperty("gists_url")
        private String gistsUrl;

        @JsonProperty("starred_url")
        private String starredUrl;

        @JsonProperty("subscriptions_url")
        private String subscriptionsUrl;

        @JsonProperty("organizations_url")
        private String organizationsUrl;

        @JsonProperty("repos_url")
        private String reposUrl;

        @JsonProperty("events_url")
        private String eventsUrl;

        @JsonProperty("received_events_url")
        private String receivedEventsUrl;

        private String type;

        @JsonProperty("site_admin")
        private boolean siteAdmin;
    }

    @Data
    public static class Links {
        private Link html;

        @JsonProperty("pull_request")
        private Link pullRequest;
    }

    @Data
    public static class Link {
        private String href;
    }
}
