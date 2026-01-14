package com.capstone.global.ratelimit.filter;

import com.capstone.global.jwt.JwtUtil;
import com.capstone.global.ratelimit.config.RateLimitConfig;
import com.capstone.global.ratelimit.enums.RateLimitKeyType;
import com.capstone.global.ratelimit.service.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    
    private final RateLimitService rateLimitService;
    private final JwtUtil jwtUtil;

    private static final List<String> EXCLUDE_PATHS = List.of(
        "/actuator",
        "/api/actuator",  
        "/swagger-ui",
        "/api/swagger-ui",  
        "/v3/api-docs",
        "/api/v3/api-docs",  
        "/health",
        "/api/health"  
    );

    private static final Map<String, RateLimitConfig> PATH_CONFIGS = Map.of(
        "/api/login", new RateLimitConfig(10, 3600, RateLimitKeyType.IP),
        "/api/register/new", new RateLimitConfig(5, 3600, RateLimitKeyType.IP),
        "/api/register/mail-check", new RateLimitConfig(20, 3600, RateLimitKeyType.IP),
        "/api/mypage/email/check", new RateLimitConfig(20, 3600, RateLimitKeyType.IP)
    );

    private static final RateLimitConfig DEFAULT_CONFIG = new RateLimitConfig(200, 60, RateLimitKeyType.USER);
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();

        if (EXCLUDE_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimitConfig config = PATH_CONFIGS.entrySet().stream()
            .filter(entry -> path.startsWith(entry.getKey()))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(DEFAULT_CONFIG);

        String key = generateKey(path, config.getKeyType(), request);

        if (!rateLimitService.isAllowed(key, config.getLimit(), config.getDuration())) {
            log.warn("RateLimit exceeded: path={}, key={}, limit={}/{}s", 
                path, key, config.getLimit(), config.getDuration());
            
            response.setStatus(429);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                String.format(
                    "{\"success\":false,\"code\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"요청 횟수를 초과했습니다. %d초 후 다시 시도해주세요.\"}",
                    config.getDuration()
                )
            );
            return;
        }

        long remaining = rateLimitService.getAvailableTokens(key, config.getLimit(), config.getDuration());
        response.setHeader("X-RateLimit-Limit", String.valueOf(config.getLimit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + config.getDuration()));
        
        filterChain.doFilter(request, response);
    }

    private String generateKey(String path, RateLimitKeyType keyType, HttpServletRequest request) {
        String identifier = switch (keyType) {
            case IP -> getClientIp(request);
            case USER -> getUserId(request);
            case GLOBAL -> "global";
        };
        
        return rateLimitService.generateKey(path, keyType, identifier);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getUserId(HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return "anonymous";
            }
            
            String token = authorizationHeader.substring(7);

            if (jwtUtil.isExpired(token)) {
                return "anonymous";
            }
            
            return jwtUtil.getEmail(token);
        } catch (Exception e) {
            log.debug("Failed to extract user ID from JWT token: {}", e.getMessage());
            return "anonymous";
        }
    }
}

