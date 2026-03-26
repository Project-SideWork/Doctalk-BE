package com.capstone.global.config;

import com.capstone.global.security.FeignCookieInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor cookieRequestInterceptor() {
        return new FeignCookieInterceptor();
    }
}