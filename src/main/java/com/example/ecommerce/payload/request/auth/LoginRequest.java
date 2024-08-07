package com.example.ecommerce.payload.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @Size(max = 50, message = "Username must not exceed 50 characters")
        @NotBlank(message = "Username must not be blank or null")
        String username,

        @Size(min = 6, max = 60, message = "Password must be between 6 and 60 characters")
        @NotBlank(message = "Password must not be blank or null")
        String password

) {}
