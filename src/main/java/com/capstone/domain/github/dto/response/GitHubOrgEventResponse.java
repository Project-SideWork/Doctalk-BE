package com.capstone.domain.github.dto.response;

import com.capstone.domain.github.dto.GitHubOrgEventDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GitHubOrgEventResponse {
    private int count;
    private List<GitHubOrgEventDto> items;
}
