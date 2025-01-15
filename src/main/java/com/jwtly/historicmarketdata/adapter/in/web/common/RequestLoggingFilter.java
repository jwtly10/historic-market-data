package com.jwtly.historicmarketdata.adapter.in.web.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        Instant start = Instant.now();

        MDC.put("requestId", requestId);
        log.info("Incoming request - method: {}, uri: {}, query: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString());

        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info("Request completed - requestId: {}, status: {}, duration: {}ms",
                    requestId,
                    response.getStatus(),
                    Duration.between(start, Instant.now()).toMillis());
            MDC.clear();
        }
    }
}