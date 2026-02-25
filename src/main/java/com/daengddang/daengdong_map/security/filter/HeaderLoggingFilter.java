package com.daengddang.daengdong_map.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class HeaderLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        String xCaddySource = request.getHeader("X-Caddy-Source");

        log.info("[Request] {} {} | X-Forwarded-For={} | X-Caddy-Source={}",
                method,
                uri,
                xForwardedFor,
                xCaddySource
        );

        filterChain.doFilter(request, response);
    }
}
