package com.capstone.domain.github.dto.response;

import com.capstone.domain.github.dto.GitHubPullRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GithubPrResponse {
    private int count;
    private List<GitHubPullRequestDto> items;
}
