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

        System.out.println("🔐 JwtAuthFilter triggered");

        final String authHeader = request.getHeader("Authorization");
//        System.out.println("🔎 Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if (authHeader != null) {
                System.out.println("⛔ Malformed Bearer token: " + authHeader);
            }
            filterChain.doFilter(request, response);
            return;
        }


        final String jwt = authHeader.substring(7);
        System.out.println("🔑 Extracted JWT: " + jwt);

        final String username = jwtService.extractUsername(jwt);
        System.out.println("👤 Username extracted from token: " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("✅ Authenticated user set in context: " + username);
        } else {
            System.out.println("⚠️ Username is null or already authenticated");
        }

        filterChain.doFilter(request, response);
    }
}
