package com.capstone.global.ratelimit.annotation;

import com.capstone.global.ratelimit.enums.RateLimitKeyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int limit() default 100;
    int duration() default 3600;
    RateLimitKeyType keyType() default RateLimitKeyType.USER;
    String keyPrefix() default "";
}

