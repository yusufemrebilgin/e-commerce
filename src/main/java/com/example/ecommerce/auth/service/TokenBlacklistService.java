package com.example.ecommerce.auth.service;

import java.util.concurrent.TimeUnit;

/**
 * Service interface for managing blacklisted tokens.
 */
public interface TokenBlacklistService {

    /**
     * Blacklists a token for a specified duration.
     *
     * @param token   The token to be blacklisted
     * @param timeout The duration before the token is removed from the blacklist
     * @param unit    The time unit of the timeout value
     */
    void blacklistToken(String token, long timeout, TimeUnit unit);

    /**
     * Checks if a token is currently blacklisted.
     *
     * @param token The token to check
     * @return {@code true} if the token is blacklisted, {@code false} otherwise
     */
    boolean isTokenBlacklisted(String token);

}
