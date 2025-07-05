package com.jarom.funbankapp.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitingConfig {

    @Value("${rate.limit.auth.requests:5}")
    private int authRequests;

    @Value("${rate.limit.auth.duration:60}")
    private int authDuration;

    @Value("${rate.limit.general.requests:100}")
    private int generalRequests;

    @Value("${rate.limit.general.duration:60}")
    private int generalDuration;

    @Bean
    public Bucket authBucket() {
        Bandwidth limit = Bandwidth.classic(authRequests, Refill.greedy(authRequests, Duration.ofSeconds(authDuration)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Bean
    public Bucket generalBucket() {
        Bandwidth limit = Bandwidth.classic(generalRequests, Refill.greedy(generalRequests, Duration.ofSeconds(generalDuration)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
} 