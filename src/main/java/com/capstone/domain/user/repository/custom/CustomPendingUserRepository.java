package com.capstone.domain.user.repository.custom;

import com.capstone.domain.user.entity.PendingUser;

import java.util.Optional;

public interface CustomPendingUserRepository {
    Optional<PendingUser> findByCredentialCode(String credentialCode);
    Optional<PendingUser> findByProjectAndUser(String projectId, String userId);
}
