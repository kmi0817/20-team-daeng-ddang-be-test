package com.daengddang.daengdong_map.ratelimit;

public record TokenBucketDecision(
        boolean allowed,
        long retryAfterSeconds
) {
    public static TokenBucketDecision allowedDecision() {
        return new TokenBucketDecision(true, 0L);
    }

    public static TokenBucketDecision deniedDecision(long retryAfterSeconds) {
        return new TokenBucketDecision(false, retryAfterSeconds);
    }
}
