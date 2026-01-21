package com.capstone.domain.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitHubOrgDto {
    private String login;
    private Long id;

    private String url;

    @JsonProperty("avatar_url")
    private String avatarUrl;

}
