package com.capstone.domain.github.util;

import com.capstone.domain.github.dto.response.GithubInfoResponse;
import com.capstone.domain.github.service.GithubTokenClient;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GithubInformationManager {

    private final Cache<Long, GithubInfoResponse> githubInfoCache;
    private final GithubTokenClient githubTokenClient;

    public void ensureValidToken(Long userId) {
        if (githubInfoCache.getIfPresent(userId) == null) {
            GithubInfoResponse infoDto = githubTokenClient.getGithubToken(userId);
            githubInfoCache.put(userId, infoDto);
        }
    }

    public String getToken(Long userId) {
        return Objects.requireNonNull(githubInfoCache.getIfPresent(userId)).githubAccessToken();
    }
}