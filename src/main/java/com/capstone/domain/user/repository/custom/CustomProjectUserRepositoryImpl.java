package com.capstone.domain.user.repository.custom;

import com.capstone.domain.project.dto.request.ProjectAuthorityRequest;
import com.capstone.domain.project.entity.Project;
import com.capstone.domain.user.entity.ProjectUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomProjectUserRepositoryImpl implements CustomProjectUserRepository{

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ProjectUser> findUserIdAndRoleByProjectIdIn(List<String> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return List.of();
        }
        Query query = new Query(Criteria.where("projectId").in(projectIds));
        return mongoTemplate.find(query, ProjectUser.class);
    }

    @Override
    public List<String> findUserIdByProjectId(String projectId) {
        Query query = new Query(Criteria.where("projectId").is(projectId));
        query.fields().include("userId");

        List<ProjectUser> projectUsers = mongoTemplate.find(query, ProjectUser.class);
        return projectUsers.stream()
            .map(ProjectUser::getUserId) // 또는 getEmail(), 실제 필드명에 따라 변경
            .toList();
    }
    @Override
    public List<Project> findProjectsByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));

        List<ProjectUser> projectUsers = mongoTemplate.find(query, ProjectUser.class);

        List<String> projectIds = projectUsers.stream()
            .map(ProjectUser::getProjectId)
            .toList();
        if (projectIds.isEmpty()) {
            return List.of();
        }
        Query projectQuery = new Query(Criteria.where("_id").in(projectIds));
        return mongoTemplate.find(projectQuery, Project.class);
    }

    @Override
    public List<ProjectUser> findUserIdAndRoleByProjectId(String projectId) {
        Query query = new Query(Criteria.where("projectId").is(projectId));
        List<ProjectUser> projectUsers = mongoTemplate.find(query, ProjectUser.class);
        return projectUsers;
    }

    @Override
    public boolean existsByProjectIdAndUserId(String projectId, String userId) {
        Query query = new Query();
        query.addCriteria(
            Criteria.where("projectId").is(projectId)
                .and("userId").is(userId)
        );
        return mongoTemplate.exists(query, ProjectUser.class);
    }
}
