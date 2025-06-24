# Backend Documentation

## Overview
This document covers the backend of FunBankApp, built with Java Spring Boot and Gradle.

## Setup
1. Install Java 21 (Amazon Corretto recommended)
2. Install Gradle
3. Configure your database in `application.properties`
4. Run migrations or create schema
5. Start the backend:
   ```sh
   ./gradlew bootRun
   ```
6. Access Swagger UI at `http://localhost:8080/swagger-ui.html`

## Architecture
- **Framework:** Spring Boot
- **Build Tool:** Gradle
- **Database:** MySQL (JDBC)
- **Security:** JWT, BCrypt
- **API Docs:** Swagger/OpenAPI

## Key Files
- `src/main/java/` — Java source code
- `src/main/resources/` — Config and static files
- `build.gradle` — Build config

## Useful Links
- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Gradle Docs](https://docs.gradle.org/current/userguide/userguide.html)
- [Swagger/OpenAPI Docs](https://swagger.io/docs/)

## Usage
- Register/login users
- Manage accounts (create, deposit, withdraw, transfer)
- Financial analysis (planned)

## Testing
- Run tests: `./gradlew test`
- Test files: `src/test/java/` 