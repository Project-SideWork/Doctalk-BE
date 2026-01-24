package com.capstone.domain.github.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

public class HttpSetter {

    private static HttpHeaders githubHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(
                List.of(MediaType.valueOf("application/vnd.github+json"))
        );
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public static HttpEntity<Void> githubEntity(String token) {
        return new HttpEntity<>(githubHeaders(token));
    }

    public static <T> HttpEntity<T> githubEntity(String token, T body) {
        return new HttpEntity<>(body, githubHeaders(token));
    }

}