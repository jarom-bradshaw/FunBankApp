# Issue 1.3: Add Rate Limiting

## Description
Authentication endpoints (`/api/auth/login`, `/api/auth/register`) do not have rate limiting, making them vulnerable to brute force attacks.

## Current Problems
- No rate limiting on authentication endpoints
- Vulnerable to brute force attacks
- No protection against DoS attacks
- Account enumeration possible

## Impact
- Brute force attacks on login
- Account enumeration attacks
- Potential DoS attacks
- Security vulnerabilities

## Solution
- Implement rate limiting for auth endpoints
- Use Spring Security rate limiting
- Add CAPTCHA for repeated failures
- Configure different limits for different endpoints

## Tasks
- [x] Add rate limiting dependencies
- [x] Create rate limiting configuration
- [x] Implement rate limiting for auth endpoints
- [x] Add rate limiting for other sensitive endpoints
- [x] Configure rate limiting thresholds
- [x] Test rate limiting functionality

## Files to be Modified
- `build.gradle` (add dependencies)
- `src/main/java/com/jarom/funbankapp/security/RateLimitingConfig.java` (new)
- `src/main/java/com/jarom/funbankapp/security/SecurityConfig.java`
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`

## Priority: Critical Security 