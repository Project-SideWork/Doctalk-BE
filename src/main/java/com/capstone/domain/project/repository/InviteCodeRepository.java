package com.capstone.domain.project.repository;

import com.capstone.domain.project.entity.InviteCode;
import com.capstone.domain.project.repository.custom.CustomInviteCodeRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteCodeRepository extends MongoRepository<InviteCode, String>, CustomInviteCodeRepository {
}
