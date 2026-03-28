package com.capstone.domain.user.repository;

import com.capstone.domain.user.entity.ProjectUser;
import com.capstone.domain.user.repository.custom.CustomProjectUserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectUserRepository extends MongoRepository<ProjectUser, String>, CustomProjectUserRepository {
    List<ProjectUser> findByProjectId(String projectId);
    List<ProjectUser> findByUserId(String email);
    Optional<ProjectUser> findByProjectIdAndUserId(String projectId, String userId);
}
