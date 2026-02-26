package com.capstone.domain.auth.token.controller;

import com.capstone.docs.TokenControllerDocs;
import com.capstone.global.jwt.CookieUtil;
import com.capstone.global.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController implements TokenControllerDocs {
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshAccessToken(@Parameter(description = "리프레쉬 토큰만 전달하면 됨") @RequestHeader("Authorization") String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        String newAccessToken = jwtUtil.reIssueToken(refreshToken);
        response.addCookie(cookieUtil.createAccessCookie(newAccessToken));
        return ResponseEntity.ok()
                .header("refresh", refreshToken)
                .header("access", newAccessToken)
                .build();
    }
}
