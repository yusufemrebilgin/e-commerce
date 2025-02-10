package com.example.ecommerce.auth.service;

import com.example.ecommerce.auth.exception.InvalidJwtTokenException;
import com.example.ecommerce.auth.exception.TokenRevokedException;
import com.example.ecommerce.auth.payload.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for handling token operations, including token generation, validation,
 * extraction, and revocation. This service manages both access tokens and refresh tokens.
 */
public interface TokenService {

    /**
     * Generates an access token and refresh token pair for the given user.
     *
     * @param userDetails the user details for whom the tokens are generated
     * @return a {@link TokenResponse} containing the access and refresh tokens
     */
    TokenResponse generateTokenPair(UserDetails userDetails);

    /**
     * Refreshes the token pair using a valid refresh token.
     *
     * @param refreshToken the refresh token used to generate a new token pair
     * @return a {@link TokenResponse} containing the new access and refresh tokens
     * @throws InvalidJwtTokenException if the token is invalid or expired
     * @throws TokenRevokedException    if the refresh token is revoked
     */
    TokenResponse refreshTokenPair(String refreshToken);

    /**
     * Validates a given token to check its authenticity and expiration.
     *
     * @param token the token to validate
     * @throws InvalidJwtTokenException if the token is malformed, expired, or unsupported
     */
    void validateToken(String token);

    /**
     * Revokes a specific refresh token by marking it as revoked in the database.
     *
     * @param token the refresh token to be revoked
     */
    void revokeToken(String token);

    /**
     * Revokes all active refresh tokens associated with a specific user.
     *
     * @param username the username whose active tokens should be revoked
     */
    void revokeAllTokensForUser(String username);

    /**
     * Extracts the token from the HTTP request's Authorization header.
     *
     * @param httpRequest the HTTP request containing the Authorization header
     * @return the extracted token, or null if the header is missing or invalid
     */
    String extractToken(HttpServletRequest httpRequest);

    /**
     * Extracts the username from a given token.
     *
     * @param token the token from which to extract the username
     * @return the username contained in the token
     */
    String extractUsername(String token);

}
