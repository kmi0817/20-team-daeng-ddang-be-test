package com.daengddang.daengdong_map.ratelimit;

public record RateLimitDecision(
        RateLimitRule rule,
        long retryAfterSeconds
) {
}
