package com.jarom.funbankapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Order(0)
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private final Map<String, RequestCounter> ipRequestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) {
            String ip = request.getRemoteAddr();
            RequestCounter counter = ipRequestCounts.computeIfAbsent(ip, k -> new RequestCounter());
            synchronized (counter) {
                long now = Instant.now().getEpochSecond();
                if (now - counter.timestamp >= 60) {
                    counter.timestamp = now;
                    counter.count = 1;
                } else {
                    counter.count++;
                }
                if (counter.count > MAX_REQUESTS_PER_MINUTE) {
                    response.setStatus(429);
                    response.getWriter().write("Too many requests. Please try again later.");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private static class RequestCounter {
        long timestamp = Instant.now().getEpochSecond();
        int count = 0;
    }
} 