package com.example.ecommerce.auth.payload.request;

import jakarta.validation.constraints.NotBlank;

/**
 * A request record to refresh a token pair.
 */
public record RefreshTokenRequest(

        @NotBlank(message = "Refresh token is required. Please provide a valid token.")
        String refreshToken

) {}
