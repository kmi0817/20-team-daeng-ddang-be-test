package com.daengddang.daengdong_map.ratelimit;

import com.daengddang.daengdong_map.common.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final boolean enabled;

    public RateLimitFilter(
            RateLimitService rateLimitService,
            @Value("${ratelimit.enabled:true}") boolean enabled
    ) {
        this.rateLimitService = rateLimitService;
        this.enabled = enabled;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<RateLimitDecision> decision = rateLimitService.check(request);
        if (decision.isPresent()) {
            RateLimitDecision denied = decision.get();
            response.setStatus(ErrorCode.TOO_MANY_REQUESTS.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Retry-After", String.valueOf(denied.retryAfterSeconds()));
            response.getWriter().write(ErrorCode.TOO_MANY_REQUESTS.toResponseJson());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
