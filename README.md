# Overview

I have a strong passion for finance and aspire to work in the fintech industry. Recently, my brother, an architect for the state of Nevada, expressed interest in renting and building a 4-plex he designed. This inspired me to analyze the financial aspects of such an investment using a spreadsheet.

To take this further, I decided to develop a mini hybrid banking application. This app is designed to connect with real banks and provide a secure environment for user data. It includes features such as balance calculations and high-level summaries using APIs integrated with Ollama.

I have implemented secure password hashing and created comprehensive Swagger documentation for the APIs I developed.

[Software Demo Video](https://youtu.be/hWKAgCgMUdQ)

# Setup Instructions

## Prerequisites
- Java 21 (Amazon Corretto recommended)
- Gradle
- MySQL server (or compatible database)

## Backend Setup
1. Clone the repository.
2. Configure your database connection in `application.properties` (not included here; see Spring Boot docs).
3. Run database migrations or create the required schema/tables.
4. Build and run the backend:
   ```sh
   ./gradlew bootRun
   ```
5. Access Swagger UI at `http://localhost:8080/swagger-ui.html` for API docs and testing.

## Running Tests
```sh
./gradlew test
```

# Features
- User registration and login with JWT authentication
- Secure password hashing (BCrypt)
- Account creation, deposit, withdraw, and transfer endpoints
- Transaction logging
- API documentation via Swagger/OpenAPI
- (Planned) Financial analysis using Ollama API

# API Usage Examples

## Register
```
POST /api/users/register
{
  "username": "alice",
  "password": "password123"
}
```

## Login
```
POST /api/users/login
{
  "username": "alice",
  "password": "password123"
}
Response: Bearer <jwt-token>
```

## Get Accounts (Authenticated)
```
GET /api/accounts
Authorization: Bearer <jwt-token>
```

## Deposit
```
POST /api/accounts/deposit
Authorization: Bearer <jwt-token>
{
  "accountId": 1,
  "amount": 100.00,
  "description": "Initial deposit"
}
```

## Withdraw
```
POST /api/accounts/withdraw
Authorization: Bearer <jwt-token>
{
  "accountId": 1,
  "amount": 50.00,
  "description": "ATM withdrawal"
}
```

## Transfer
```
POST /api/accounts/transfer
Authorization: Bearer <jwt-token>
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 25.00,
  "description": "Transfer to savings"
}
```

# Development Environment

- **Database**: Hosted on my SQL server, using JDBC for database connectivity to ensure efficient runtime performance.
- **Framework**: Java Spring Boot
- **JDK**: Amazon Corretto 21
- **Build Tool**: Gradle
- **Libraries**: Bean, Lombok, and others (to be documented)

# Key Features

- **Secure Authentication**: Implemented password hashing using BCrypt to ensure user data security.
- **API Documentation**: Automated Swagger documentation for easy API reference and testing.
- TODO: **Financial Analysis**: Integrated with Ollama API to perform financial calculations and provide summaries.

# Useful Websites

- [How to hash user data and passwords](https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt)
- [How to set up JWT Authentication](https://www.javaguides.net/2024/01/spring-boot-security-jwt-tutorial.html)
- [How to automate Swagger Documentation](https://swagger.io/docs/)
- [Best Tips for Springboot Security (Bcrypt, etc.)](https://github.com/ZeroSTF/Spring-Security-Best-Practices)

# Future Work

Note: This product is in the making. I am currently only locally hosting it and will later push it to the cloud.

I have outlined future enhancements on a Trello board. As I am currently working on this project alone, I am seeking collaborators, especially those skilled in frontend development and graphic design. If interested, please reach out to me at jarombrads@gmail.com. I would love to work with you to bring this application to fruition.
