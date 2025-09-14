package com.capstone.domain.project.repository.custom;

import com.capstone.domain.project.entity.InviteCode;

import java.util.Optional;

public interface CustomInviteCodeRepository {
    Optional<InviteCode> findByCredentialCode(String credentialCode);
}
