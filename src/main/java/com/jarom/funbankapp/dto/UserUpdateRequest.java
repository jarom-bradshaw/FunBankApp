package com.jarom.funbankapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Request for updating an existing user profile")
public class UserUpdateRequest {

    @Schema(description = "User's first name", example = "John")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    @Email(message = "Email must be a valid email address")
    private String email;

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
} 