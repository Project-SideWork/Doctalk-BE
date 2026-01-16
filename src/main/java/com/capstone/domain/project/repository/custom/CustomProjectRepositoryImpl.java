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

}
