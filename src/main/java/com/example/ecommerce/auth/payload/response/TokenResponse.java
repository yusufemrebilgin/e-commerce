package com.example.ecommerce.auth.payload.response;

/**
 * A response containing authentication tokens and expiration time.
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresInMs
) {}
