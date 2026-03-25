package com.capstone.domain.github.util;

import com.capstone.domain.github.dto.response.GithubInfoResponse;
import com.capstone.domain.github.service.GithubTokenClient;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubInformationManager {

    private final Cache<Long, GithubInfoResponse> githubInfoCache;
    private final GithubTokenClient githubTokenClient;

    public void ensureValidToken(Long userId) {
        log.info("ensureValidToken called with " + userId);
        if (githubInfoCache.getIfPresent(userId) == null) {
            log.info("githubInfoCache.getIfPresent(userId) == null " + userId);
            GithubInfoResponse infoDto = githubTokenClient.getGithubToken(userId);
            log.info("GROWP API CALLED" + infoDto.githubId());
            log.info("GROWP API CALLED2" + infoDto.githubAccessToken());
            githubInfoCache.put(userId, infoDto);
        }
    }

    public String getToken(Long userId) {
        log.info("getTOken" + githubInfoCache.getIfPresent(userId).githubAccessToken());
        return Objects.requireNonNull(githubInfoCache.getIfPresent(userId)).githubAccessToken();
    }
}