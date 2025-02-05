package com.example.ecommerce.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

/**
 * A request record to register a new user.
 */
@Builder
public record UserRegistrationRequest(

        @Size(max = 100, message = "Name must be at most 100 characters long.")
        @NotBlank(message = "Name is required. Please provide a valid name.")
        String name,

        @Size(max = 50, message = "Username must be at most 50 characters long.")
        @NotBlank(message = "Username is required. Please provide a valid username.")
        String username,

        @Size(min = 6, max = 60, message = "Password must be between 6 and 60 characters long.")
        @NotBlank(message = "Password is required. Please provide a valid password.")
        String password,

        @Email(message = "Please provide a valid email address.")
        @NotBlank(message = "Email is required. Please provide a valid email.")
        String email,

        Set<String> roles

) {}
