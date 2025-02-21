package com.example.ecommerce.auth.payload.request;

import jakarta.validation.constraints.NotBlank;

/**
 * A request record to log out user.
 */
public record LogoutRequest(

        @NotBlank(message = "Refresh token is required for log out operation. Please provide a valid token.")
        String refreshToken

) {}
