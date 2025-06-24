package com.jarom.funbankapp.security;

import com.jarom.funbankapp.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private Key key;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            System.out.println("🔍 JWT Service - attempting to parse token: " + token.substring(0, Math.min(20, token.length())) + "...");
            System.out.println("🔍 JWT Service - token length: " + token.length());
            System.out.println("🔍 JWT Service - key initialized: " + (key != null));
            
            String username = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            
            System.out.println("✅ JWT Service - successfully extracted username: " + username);
            return username;
        } catch (Exception e) {
            System.out.println("❌ JWT parsing failed: " + e.getMessage());
            System.out.println("❌ Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            return null;
        }
    }


    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username);
    }
}
