package com.jarom.funbankapp.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact();
        contact.setEmail("jarombrads@gmail.com");
        contact.setName("FunBankApp API Support");
        contact.setUrl("https://github.com/yourusername/FunBankApp");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("FunBankApp API")
                .version("1.0.0")
                .contact(contact)
                .description("""
                    # FunBankApp API Documentation
                    
                    This API provides endpoints for FunBankApp, a comprehensive personal finance management application.
                    
                    ## Features
                    - **Account Management**: Create and manage multiple bank accounts
                    - **Transactions**: Deposit, withdraw, and transfer funds between accounts
                    - **Authentication**: Secure JWT-based authentication system
                    - **Financial Analysis**: AI-powered financial insights (coming soon)
                    
                    ## Authentication
                    Most endpoints require authentication using JWT tokens. Include the token in the Authorization header:
                    ```
                    Authorization: Bearer <your-jwt-token>
                    ```
                    
                    ## Getting Started
                    1. Register a new user using `/api/auth/register`
                    2. Login to get a JWT token using `/api/auth/login`
                    3. Use the token to access protected endpoints
                    
                    ## Rate Limiting
                    API requests are limited to prevent abuse. Please implement appropriate caching in your applications.
                    """)
                .license(mitLicense);

        // Define security scheme for JWT (available for endpoints that need it)
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token for authentication");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer))
                .schemaRequirement("bearerAuth", securityScheme);
    }
} 