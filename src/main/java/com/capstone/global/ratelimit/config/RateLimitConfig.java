package com.capstone.global.ratelimit.config;

import com.capstone.global.ratelimit.enums.RateLimitKeyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RateLimitConfig {
    private final int limit;
    private final int duration; // 초 단위
    private final RateLimitKeyType keyType;
}

