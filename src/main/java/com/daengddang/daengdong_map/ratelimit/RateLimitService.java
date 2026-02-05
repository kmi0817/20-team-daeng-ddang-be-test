package com.daengddang.daengdong_map.ratelimit;

import com.daengddang.daengdong_map.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

@Service
public class RateLimitService {

    private final List<RateLimitRule> rules;
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public RateLimitService(RateLimitPolicy policy) {
        this.rules = policy.rules();
    }

    public Optional<RateLimitDecision> check(HttpServletRequest request) {
        RateLimitRule rule = findRule(request);
        if (rule == null) {
            return Optional.empty();
        }

        String key = resolveKey(request);
        String bucketKey = rule.id() + ":" + key;
        TokenBucket bucket = buckets.computeIfAbsent(
                bucketKey,
                ignored -> new TokenBucket(rule.limit(), rule.windowSeconds())
        );

        TokenBucketDecision decision = bucket.tryConsume();
        if (decision.allowed()) {
            return Optional.empty();
        }

        return Optional.of(new RateLimitDecision(rule, decision.retryAfterSeconds()));
    }

    private RateLimitRule findRule(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        for (RateLimitRule rule : rules) {
            if (!rule.method().equalsIgnoreCase(method)) {
                continue;
            }
            if (pathMatcher.match(rule.pattern(), path)) {
                return rule;
            }
        }
        return null;
    }

    private String resolveKey(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUser authUser) {
            return "user:" + authUser.getUserId();
        }
        return "ip:" + resolveClientIp(request);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return comma > 0 ? forwarded.substring(0, comma).trim() : forwarded.trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
