package com.capstone.domain.user.repository.custom;

import com.capstone.domain.project.entity.InviteCode;
import com.capstone.domain.user.entity.PendingUser;

import java.util.Optional;

public interface CustomPendingUserRepository {
    Optional<PendingUser> findByCredentialCode(InviteCode credentialCode);
    Optional<PendingUser> findByProjectAndUser(String projectId, String userId);
}
