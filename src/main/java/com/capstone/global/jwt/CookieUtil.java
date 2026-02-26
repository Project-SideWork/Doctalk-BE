package com.capstone.global.jwt;

import com.capstone.domain.auth.exception.InvalidTokenException;
import com.capstone.domain.auth.token.message.TokenMessages;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class CookieUtil {
    private static final int COOKIE_EXPIRE_TIME = 30 * 60; // 30분
    private static final int ACCESS_COOKIE_MAX_AGE = 15 * 60; // 15분

    public Cookie createCookie(String key, String value) {
        return createCookie(key, value, COOKIE_EXPIRE_TIME);
    }

    public Cookie createCookie(String key, String value, int maxAgeSeconds) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);// 임시 false
        cookie.setAttribute("SameSite", "LAX"); //임시 코드
        return cookie;
    }

    public Cookie createAccessCookie(String accessToken) {
        return createCookie("access", accessToken, ACCESS_COOKIE_MAX_AGE);
    }

    public ResponseCookie createResponseCookie(String refreshToken){
        return ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict") // CSRF 방어
                .path("/")      // 모든 경로에서 유효
                .maxAge(COOKIE_EXPIRE_TIME) // 7일 유지
                .build();
    }

    public Cookie createCsrfCookie(HttpServletRequest request, HttpServletResponse response){
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrfToken.getToken());
        csrfCookie.setSecure(false);
        csrfCookie.setPath("/");
        response.addCookie(csrfCookie);

        return csrfCookie;
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Optional.ofNullable(request.getCookies())
                .ifPresent(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> name.equals(cookie.getName()))
                        .forEach(cookie -> {
                            cookie.setValue("");  // 필요하지 않음, setMaxAge(0)으로 충분함
                            cookie.setPath("/");
                            cookie.setMaxAge(0);  // 쿠키 삭제
                            cookie.setHttpOnly(true);
                            cookie.setSecure(request.isSecure());
                            response.addCookie(cookie);
                        }));
    }

    public static Optional<String> getRefreshTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public static String findTokenOrThrow(HttpServletRequest request){
        return getRefreshTokenFromRequest(request)
                .orElseThrow(() -> new InvalidTokenException(TokenMessages.REFRESH_NOT_FOUND));
    }
    public static Optional<String> getAccessTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
            .filter(cookie -> "access".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst();
    }
}
