package com.capstone.global.security;

import java.util.List;

// 공통 상수 클래스로 분리
public class SecurityConstants {
    public static final List<String> PUBLIC_PATHS = List.of(
            "/oauth2/**",
            "/register/**",
            "/login",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/socket/**",
            "/editing",
            "/mypage/email/avail",
            "/mypage/password/new",
            "/mypage/email/check",
            "/project/invite/accept",
            "/oauth/login",
            "/health",
            "/actuator/health",
            "/github/webhook"
    );
}
