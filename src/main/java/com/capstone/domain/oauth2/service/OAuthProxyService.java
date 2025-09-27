package com.capstone.domain.oauth2.service;

import com.capstone.domain.AI.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OAuthProxyService {
    private final WebClient oAuth2Client;

    public void sendOAuthRequest(String provider) {
        oAuth2Client.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("docktalk.co.kr")
                        .path("/oauth2/authorization/{provider}")
                        .queryParam("access_type", "offline")
                        .queryParam("mode", "login")
                        .queryParam("redirect_uri", "https://docktalk.co.kr")
                        .build(provider)
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
