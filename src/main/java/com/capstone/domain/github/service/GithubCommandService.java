package com.capstone.domain.github.service;

import com.capstone.domain.github.dto.GithubIssueWebhookDto;
import com.capstone.domain.github.dto.GithubPRReviewWebhookDto;
import com.capstone.domain.github.dto.GithubPRWebhookDto;
import com.capstone.domain.project.entity.*;
import com.capstone.domain.project.repository.ProjectRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubCommandService {
    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper;


    public void createIssue(String payload) throws JsonProcessingException {
        GithubIssueWebhookDto dto = objectMapper.readValue(payload, GithubIssueWebhookDto.class);
        String orgName = dto.getOrganization().getLogin();
        Project project = projectRepository.findByGithubOrganization(orgName);
        ProjectOrganization organization = project.getProjectOrganizations()
                .stream()
                .filter(org -> org.getOrgName().equals(orgName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        ProjectGithubIssue projectGithubIssue = ProjectGithubIssue.create(
                dto.getIssue().getId(), dto.getRepository().getId(),
                dto.getIssue().getNumber(), dto.getIssue().getTitle(),
                dto.getIssue().getState(), dto.getIssue().getUser().getLogin(),
                dto.getIssue().getCreatedAt()
        );

        organization.addGithubIssue(projectGithubIssue);

        projectRepository.save(project);
    }

    public void createPullRequest(String payload) throws JsonProcessingException {
        GithubPRWebhookDto dto = objectMapper.readValue(payload, GithubPRWebhookDto .class);
        String orgName = dto.getOrganization().getLogin();
        Project project = projectRepository.findByGithubOrganization(orgName);
        ProjectOrganization organization = project.getProjectOrganizations()
                .stream()
                .filter(org -> org.getOrgName().equals(orgName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        ProjectGithubPullRequest pr = ProjectGithubPullRequest.create(
                dto.getPullRequest().getId(),
                dto.getRepository().getId(),
                dto.getPullRequest().getNumber(),
                dto.getPullRequest().getTitle(),
                dto.getPullRequest().getState(),
                dto.getPullRequest().getUser().getLogin(),
                dto.getPullRequest().getCreatedAt().toInstant(),
                dto.getPullRequest().getClosedAt() != null
                        ? dto.getPullRequest().getClosedAt().toInstant()
                        : null,
                dto.getPullRequest().getMergedAt() != null
                        ? dto.getPullRequest().getMergedAt().toInstant()
                        : null,
                dto.getPullRequest().getMerged(),
                dto.getPullRequest().getCommits(),
                dto.getPullRequest().getAdditions(),
                dto.getPullRequest().getDeletions(),
                dto.getPullRequest().getChangedFiles(),
                dto.getPullRequest().getComments(),
                dto.getPullRequest().getReviewComments()
        );

        organization.addGithubPullRequest(pr);

        projectRepository.save(project);
    }

    public void createPullRequestReview(String payload) throws JsonProcessingException {
        GithubPRReviewWebhookDto dto = objectMapper.readValue(payload, GithubPRReviewWebhookDto .class);
        String orgName = dto.getOrganization().getLogin();
        Project project = projectRepository.findByGithubOrganization(orgName);
        ProjectOrganization organization = project.getProjectOrganizations()
                .stream()
                .filter(org -> org.getOrgName().equals(orgName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        ProjectGithubPullRequestReview review =
                ProjectGithubPullRequestReview.create(
                        dto.getReview().getId(),
                        dto.getRepository().getId(),
                        dto.getPullRequest().getId(),
                        dto.getPullRequest().getNumber(),
                        dto.getReview().getUser().getLogin(),
                        dto.getReview().getState(),
                        dto.getReview().getBody(),
                        dto.getReview().getSubmittedAt().toInstant()
                );

        organization.addGithubPullRequestReview(review);
        projectRepository.save(project);
    }

}
