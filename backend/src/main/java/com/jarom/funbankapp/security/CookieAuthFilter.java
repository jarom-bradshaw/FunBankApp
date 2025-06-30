package com.jarom.funbankapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Collections;

@Component
public class CookieAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public CookieAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Skip authentication for registration, login, logout, and CSRF token endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/users/register") || 
            requestURI.equals("/api/users/login") || 
            requestURI.equals("/api/users/logout") ||
            requestURI.equals("/api/users/csrf-token")) {
            System.out.println("🔓 Skipping cookie auth for: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("🔐 CookieAuthFilter triggered for: " + requestURI);

        // Extract JWT token from HttpOnly cookie
        String jwt = extractTokenFromCookie(request);
        
        if (jwt == null) {
            System.out.println("⛔ No authentication cookie found");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: No authentication cookie");
            return;
        }

        System.out.println("🔑 Extracted JWT from cookie: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");
        System.out.println("🔑 Full JWT length: " + jwt.length() + " characters");

        final String username = jwtService.extractUsername(jwt);
        System.out.println("👤 Username extracted from token: " + username);

        if (username == null) {
            System.out.println("❌ Invalid JWT token in cookie - username extraction failed");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid authentication cookie");
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("✅ Authenticated user set in context: " + username);
        } else {
            System.out.println("⚠️ User already authenticated");
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        
        if (cookies == null) {
            System.out.println("🍪 No cookies found in request");
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("auth-token".equals(cookie.getName())) {
                String token = cookie.getValue();
                if (token != null && !token.isEmpty()) {
                    System.out.println("🍪 Found auth-token cookie");
                    return token;
                } else {
                    System.out.println("🍪 auth-token cookie is empty");
                }
            }
        }

        System.out.println("🍪 auth-token cookie not found");
        return null;
    }
} 