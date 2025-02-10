package com.example.ecommerce.auth.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for JWT settings.
 * <p>
 * This class holds the JWT-related properties loaded from {@code application.yaml}
 * and provides validation to ensure correct values.
 */
@Getter
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * The secret key used to sign JWT tokens.
     * It should be a Base64-encoded string.
     */
    @NotBlank(message = "JWT secret key cannot be blank")
    private final String secret;

    /**
     * The expiration time (in milliseconds) for access tokens.
     * Typically short-lived.
     */
    @Positive(message = "Access token expiration time must be positive")
    private final long accessExpInMs;

    /**
     * The expiration time (in milliseconds) for refresh tokens.
     * Typically long-lived.
     */
    @Positive(message = "Refresh token expiration time must be positive")
    private final long refreshExpInMs;

    @ConstructorBinding
    public JwtProperties(String secret, long accessExpInMs, long refreshExpInMs) {
        this.secret = secret;
        this.accessExpInMs = accessExpInMs;
        this.refreshExpInMs = refreshExpInMs;
    }

}
