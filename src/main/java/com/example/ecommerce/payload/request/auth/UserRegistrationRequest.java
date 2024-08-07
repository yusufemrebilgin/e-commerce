package com.example.ecommerce.payload.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UserRegistrationRequest(

        @Size(max = 100, message = "Name must not exceed 100 characters")
        @NotBlank(message = "Name must not be blank or null")
        String name,

        @Size(max = 50, message = "Username must not exceed 50 characters")
        @NotBlank(message = "Username must not be blank or null")
        String username,

        @Size(min = 6, max = 60, message = "Password must be between 6 and 60 characters")
        @NotBlank(message = "Password must not be blank or null")
        String password,

        @Email(message = "Email should be valid")
        @NotBlank(message = "Email must not be blank or null")
        String email,

        Set<String> roles

) {}
