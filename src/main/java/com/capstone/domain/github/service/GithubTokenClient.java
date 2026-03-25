package com.capstone.domain.github.service;

import com.capstone.domain.github.dto.response.GithubInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", url = "${auth.service.url}")
public interface GithubTokenClient {

    @GetMapping("/api/v1/user/github")
    GithubInfoResponse getGithubToken(@RequestHeader("X-User-Id") Long userId);
}