package com.daengddang.daengdong_map.ratelimit;

public record RateLimitRule(
        String id,
        String method,
        String pattern,
        int limit,
        long windowSeconds
) {
}
