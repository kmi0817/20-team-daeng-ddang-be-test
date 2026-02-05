package com.daengddang.daengdong_map.ratelimit;

import java.util.concurrent.TimeUnit;

public class TokenBucket {

    private final long capacity;
    private final double refillTokensPerNano;
    private double tokens;
    private long lastRefillNanos;

    public TokenBucket(long capacity, long windowSeconds) {
        this.capacity = capacity;
        this.refillTokensPerNano = capacity / (double) TimeUnit.SECONDS.toNanos(windowSeconds);
        this.tokens = capacity;
        this.lastRefillNanos = System.nanoTime();
    }

    public synchronized TokenBucketDecision tryConsume() {
        long now = System.nanoTime();
        refill(now);

        if (tokens >= 1D) {
            tokens -= 1D;
            return TokenBucketDecision.allowedDecision();
        }

        long retryAfterSeconds = computeRetryAfterSeconds(now);
        return TokenBucketDecision.deniedDecision(retryAfterSeconds);
    }

    private void refill(long now) {
        long elapsedNanos = now - lastRefillNanos;
        if (elapsedNanos <= 0) {
            return;
        }
        double refill = elapsedNanos * refillTokensPerNano;
        if (refill <= 0D) {
            return;
        }
        tokens = Math.min(capacity, tokens + refill);
        lastRefillNanos = now;
    }

    private long computeRetryAfterSeconds(long now) {
        if (refillTokensPerNano <= 0D) {
            return 1L;
        }
        double needed = 1D - tokens;
        long nanosToNext = (long) Math.ceil(needed / refillTokensPerNano);
        long seconds = TimeUnit.NANOSECONDS.toSeconds(nanosToNext);
        if (seconds <= 0L) {
            seconds = 1L;
        }
        return seconds;
    }
}
