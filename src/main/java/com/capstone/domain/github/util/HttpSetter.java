package com.capstone.domain.github.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HttpSetter {
    public static HttpHeaders httpPreset(String token){
        HttpHeaders httpHeaders =  new HttpHeaders();

        httpHeaders.setBearerAuth(token);
        httpHeaders.set("Accept", "application/vnd.github+json");
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
