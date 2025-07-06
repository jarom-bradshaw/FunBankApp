package com.jarom.funbankapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Skip JWT authentication for public endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/auth/register") || 
            requestURI.equals("/api/auth/login") || 
            requestURI.equals("/api/health") ||
            requestURI.startsWith("/swagger-ui") ||
            requestURI.startsWith("/v3/api-docs") ||
            requestURI.equals("/swagger-ui.html")) {
            System.out.println("🔓 Skipping JWT auth for: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("🔐 JwtAuthFilter triggered for: " + requestURI);

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if (authHeader != null) {
                System.out.println("⛔ Malformed Bearer token: " + authHeader);
            } else {
                System.out.println("⛔ No Authorization header");
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: No valid token provided");
            return;
        }

        final String jwt = authHeader.substring(7);
        System.out.println("🔑 Extracted JWT: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");
        System.out.println("🔑 Full JWT length: " + jwt.length() + " characters");

        final String username = jwtService.extractUsername(jwt);
        System.out.println("👤 Username extracted from token: " + username);

        if (username == null) {
            System.out.println("❌ Invalid JWT token - username extraction failed");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid token");
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
}
