package com.capstone.domain.project.repository.custom;


import com.capstone.domain.project.entity.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomProjectRepositoryImpl implements CustomProjectRepository{
    private final MongoTemplate mongoTemplate;

    @Override
    public Project findByProjectName(String projectName) {
        Query query = new Query(Criteria.where("projectName").is(projectName));
        return mongoTemplate.findOne(query, Project.class);
    }

    @Override
    public List<Project> findAllById(List<String> ids) {
        Query query = new Query(Criteria.where("_id").in(ids));
        return mongoTemplate.find(query, Project.class);
    }

    @Override
    public void addTaskIdAtomically(String projectId, String taskId) {
        Query query = new Query(Criteria.where("_id").is(projectId));
        Update update = new Update().addToSet("taskIds", taskId);
        mongoTemplate.updateFirst(query, update, Project.class);
    }

    @Override
    public Project findByGithubOrganization(String orgName) {
        Query query = new Query(
                Criteria.where("projectOrganizations.orgName").is(orgName)
        );

        return mongoTemplate.findOne(query, Project.class);
    }

    @Override
    public double rateMyIssueRatio(String projectId, Long githubUserId) {
        Query query = new Query(
                Criteria.where("_id").is(projectId)
        );
        Project project = mongoTemplate.findOne(query, Project.class);

        if (project == null || project.getProjectOrganizations() == null) {
            return 0;
        }

        int total = project.getProjectOrganizations().stream()
                .mapToInt(org -> org.getGithubIssues().size())
                .sum();

        int mine = project.getProjectOrganizations().stream()
                .filter(org -> org.getGithubIssues() != null)
                .flatMap(org -> org.getGithubIssues().stream())
                .filter(issue -> githubUserId.equals(issue.getIssueOpenUserId()))
                .mapToInt(issue -> 1)
                .sum();

        double rate = total == 0 ? 0 : (double) mine / total * 100;
        return Math.round(rate * 10) / 10.0;
    }


    @Override
    public double rateMyPRRatio(String projectId, Long githubUserId) {
        Query query = new Query(
                Criteria.where("_id").is(projectId)
        );
        Project project = mongoTemplate.findOne(query, Project.class);

        if (project == null || project.getProjectOrganizations() == null) {
            return 0;
        }

        int total = project.getProjectOrganizations().stream()
                .mapToInt(org -> org.getGithubPullRequests().size())
                .sum();

        int mine = project.getProjectOrganizations().stream()
                .filter(org -> org.getGithubPullRequests() != null)
                .flatMap(org -> org.getGithubPullRequests().stream())
                .filter(issue -> githubUserId.equals(issue.getPrAuthorId()))
                .mapToInt(issue -> 1)
                .sum();

        double rate = total == 0 ? 0 : (double) mine / total * 100;
        return Math.round(rate * 10) / 10.0;
    }

    @Override
    public double rateMyReviewRatio(String projectId, Long githubUserId) {
        Query query = new Query(
                Criteria.where("_id").is(projectId)
        );
        Project project = mongoTemplate.findOne(query, Project.class);

        if (project == null || project.getProjectOrganizations() == null) {
            return 0;
        }

        int total =  project.getProjectOrganizations().stream()
                .mapToInt(org -> org.getGithubPullRequestReviews().size())
                .sum();

        int mine = project.getProjectOrganizations().stream()
                .filter(org -> org.getGithubPullRequestReviews() != null)
                .flatMap(org -> org.getGithubPullRequestReviews().stream())
                .filter(review -> githubUserId.equals(review.getReviewerId()))
                .mapToInt(review -> 1)
                .sum();

        double rate = total == 0 ? 0 : (double) mine / total * 100;
        return Math.round(rate * 10) / 10.0;
    }

}
