package com.capstone.global.ratelimit.service;

import com.capstone.global.ratelimit.enums.RateLimitKeyType;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.redisson.cas.RedissonBasedProxyManager;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RateLimitService {
    private final RedissonBasedProxyManager<String> proxyManager;
    private final Map<String, Bucket> localCache = new ConcurrentHashMap<>();

    public RateLimitService(RedissonClient redissonClient) {
        Redisson redisson = (Redisson) redissonClient;
        this.proxyManager = RedissonBasedProxyManager
            .builderFor(redisson.getCommandExecutor())
            .build();
    }

    public boolean isAllowed(String key, int limit, int duration) {
        try {
            Bucket bucket = resolveBucket(key, limit, duration);
            boolean allowed = bucket.tryConsume(1);

            if (!allowed) {
                log.warn("RateLimit exceeded for key: {}, limit: {}/{}s", key, limit, duration);
            }
            return allowed;
        } catch (Exception e) {
            log.error("RateLimit check failed for key: {}", key, e);
            return true;
        }
    }

    private Bucket resolveBucket(String key, int limit, int duration) {
        String cacheKey = key + ":" + limit + ":" + duration;

        return localCache.computeIfAbsent(cacheKey, k -> {
            Bandwidth bandwidth = Bandwidth.classic(
                limit,
                Refill.intervally(limit, Duration.ofSeconds(duration))
            );

            BucketConfiguration config = BucketConfiguration.builder()
                .addLimit(bandwidth)
                .build();

            return proxyManager.builder()
                .build(key, () -> config);
        });
    }


    public String generateKey(String path, RateLimitKeyType keyType, String identifier) {
        return switch (keyType) {
            case IP -> "ratelimit:ip:" + identifier + ":" + path;
            case USER -> "ratelimit:user:" + identifier + ":" + path;
            case GLOBAL -> "ratelimit:global:" + path;
        };
    }

    public long getAvailableTokens(String key, int limit, int duration) {
        try {
            Bucket bucket = resolveBucket(key, limit, duration);
            return bucket.getAvailableTokens();
        } catch (Exception e) {
            log.error("Failed to get available tokens for key: {}", key, e);
            return limit;
        }
    }
}
