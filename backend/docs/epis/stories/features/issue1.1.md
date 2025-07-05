# Issue 1.1: Externalize Sensitive Configuration

## Description
Database credentials and JWT secrets are hardcoded in `application.properties`, which is a critical security vulnerability.

## Current Problems
- Database credentials exposed in source code
- JWT secret hardcoded in properties file
- No environment-specific configuration
- Sensitive data visible in version control

## Impact
- Database credentials are visible in source code
- Potential unauthorized database access
- JWT tokens could be forged
- Violation of security best practices

## Solution
- Move credentials to environment variables
- Create separate configuration files for different environments
- Use Spring Boot's configuration properties
- Externalize JWT secret to environment variables

## Tasks
- [x] Create environment-specific configuration files
- [x] Externalize database credentials to environment variables
- [x] Externalize JWT secret to environment variables
- [x] Update application.properties to use environment variables
- [x] Create .env.example file for documentation
- [x] Update .gitignore to exclude sensitive files

## Files to be Modified
- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties` (new)
- `src/main/resources/application-prod.properties` (new)
- `.env.example` (new)
- `.gitignore`

## Priority: Critical Security 