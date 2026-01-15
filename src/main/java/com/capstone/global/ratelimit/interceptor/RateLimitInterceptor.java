package com.capstone.global.ratelimit.interceptor;

import com.capstone.global.jwt.JwtUtil;
import com.capstone.global.ratelimit.annotation.RateLimit;
import com.capstone.global.ratelimit.enums.RateLimitKeyType;
import com.capstone.global.ratelimit.service.RateLimitService;
import com.capstone.global.security.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final RateLimitService rateLimitService;
    private final JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        
        if (rateLimit == null) {
            return true;
        }
        
        String path = request.getRequestURI();
        String keyPrefix = rateLimit.keyPrefix().isEmpty() ? path : rateLimit.keyPrefix();
        String identifier = getIdentifier(rateLimit.keyType(), request);
        RateLimitKeyType actualKeyType = getActualKeyType(rateLimit.keyType(), request);
        String key = rateLimitService.generateKey(keyPrefix, actualKeyType, identifier);

        if (!rateLimitService.isAllowed(key, rateLimit.limit(), rateLimit.duration())) {
            log.warn("RateLimit exceeded: path={}, key={}, limit={}/{}s", 
                path, key, rateLimit.limit(), rateLimit.duration());
            
            response.setStatus(429); 
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                String.format(
                    "{\"success\":false,\"code\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"요청 횟수를 초과했습니다. %d초 후 다시 시도해주세요.\"}",
                    rateLimit.duration()
                )
            );
            return false;
        }

        long remaining = rateLimitService.getAvailableTokens(key, rateLimit.limit(), rateLimit.duration());
        response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.limit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + rateLimit.duration()));
        
        return true;
    }
    private String getIdentifier(RateLimitKeyType keyType, HttpServletRequest request) {
        if (keyType == RateLimitKeyType.USER) {
            String userId = getUserId(request);
            if (userId == null || "anonymous".equals(userId)) {
                return getClientIp(request);
            }
            return userId;
        }
        return switch (keyType) {
            case IP -> getClientIp(request);
            case GLOBAL -> "global";
            case USER -> getClientIp(request);
        };
    }
    private RateLimitKeyType getActualKeyType(RateLimitKeyType keyType, HttpServletRequest request) {
        if (keyType == RateLimitKeyType.USER) {
            String userId = getUserId(request);
            if (userId == null || "anonymous".equals(userId)) {
                return RateLimitKeyType.IP;
            }
        }
        return keyType;
    }
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            ip = ip.split(",")[0].trim();
        } else {
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getEmail();
        }
        return null;
    }
}

