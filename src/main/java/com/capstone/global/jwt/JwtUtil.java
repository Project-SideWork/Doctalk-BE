package com.capstone.global.jwt;

import com.capstone.domain.auth.exception.InvalidTokenException;
import com.capstone.domain.user.entity.User;
import com.capstone.domain.user.repository.UserRepository;
import com.capstone.global.response.exception.GlobalException;
import com.capstone.global.response.status.ErrorStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class JwtUtil {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 15 * 60 * 1000; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 1000; // 1시

    private SecretKey secretKey;
    private final UserRepository userRepository;

    //검증 메서드

    public JwtUtil(@Value("${jwt.secret}") String secret, UserRepository userRepository) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.userRepository = userRepository;
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("email", String.class);
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("category", String.class);
    }

    public Boolean isExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());

        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            throw new InvalidTokenException(ErrorStatus.EMPTY_JWT.getMessage());
        }
    }

    public String createAccess(String email) {
        return Jwts.builder()
                .claim("category", "access")
                .claim("email", email)
                .claim("role", "ROLE_MANAGER")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String createRefresh(String email) {
        return Jwts.builder()
                .claim("category", "refresh")
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();
    }

    private Claims extractClaims(String token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseSignedClaims(token)
                .getBody();

    }

    public Authentication getAuthentication(String token) {
        String email = getEmail(token);
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("JWT token does not contain a valid googleId.");
        }

        // DB에서 googleId 기반으로 사용자 찾기
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with googleId: " + email);
        }


        User user = userOptional.get();
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Spring Security User 객체 생성 (googleId를 username으로 사용)
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), "", authorities
        );

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
}
