package com.capstone.domain.github.dto;

import lombok.Data;

import java.util.List;

@Data
public class GitHubPrQueryResponse {
    private int total_count;
    private boolean incomplete_results;
    private List<GitHubPullRequestDto> items;
}