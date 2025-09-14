package com.capstone.domain.project.repository.custom;

import com.capstone.domain.project.entity.InviteCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class CustomInviteCodeRepositoryImpl implements CustomInviteCodeRepository{
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<InviteCode> findByCredentialCode(String credentialCode) {
        Query query = new Query(Criteria.where("code").is(credentialCode));
        return Optional.ofNullable(mongoTemplate.findOne(query, InviteCode.class));
    }
}
