package com.capstone.domain.github.dto.response;

import com.capstone.domain.github.dto.GitHubIssueDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GithubIssueResponse {
    private int count;
    private List<GitHubIssueDto> items;
}
