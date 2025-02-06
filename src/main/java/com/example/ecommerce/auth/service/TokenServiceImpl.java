package com.example.ecommerce.auth.service;

import com.example.ecommerce.auth.config.JwtProperties;
import com.example.ecommerce.auth.exception.InvalidJwtTokenException;
import com.example.ecommerce.auth.exception.TokenRevokedException;
import com.example.ecommerce.auth.model.RefreshToken;
import com.example.ecommerce.auth.model.User;
import com.example.ecommerce.auth.model.enums.TokenType;
import com.example.ecommerce.auth.payload.response.TokenResponse;
import com.example.ecommerce.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

    // The expiration time of refresh tokens is very long, and
    // they are used to renew access tokens. For this reason,
    // it will be stored in the database.
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;

    @Override
    public TokenResponse generateTokenPair(UserDetails userDetails) {

        String accessToken = buildAccessToken(userDetails);
        String refreshToken = buildRefreshToken(userDetails);
        saveRefreshToken(refreshToken, userDetails);

        return new TokenResponse(accessToken, refreshToken, jwtProperties.getAccessExpInMs());
    }

    @Override
    public TokenResponse refreshTokenPair(String refreshToken) {
        try {
            validateToken(refreshToken);
        } catch (InvalidJwtTokenException ex) {
            revokeToken(refreshToken);
            logger.error("Cannot generate new token pair");
            throw ex;
        }

        // Called to verify that user exists
        // otherwise exception will be thrown
        User user = getUser(refreshToken);

        // Check stored token revoked or not
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(storedToken -> {
                    if (storedToken.isRevoked()) {
                        throw new TokenRevokedException();
                    }
                });

        // Revoke stored old refresh token
        revokeToken(refreshToken);

        // Generate a new token pair
        String newAccessToken = buildAccessToken(user);
        String newRefreshToken = buildRefreshToken(user);
        saveRefreshToken(newRefreshToken, user);

        return new TokenResponse(newAccessToken, newRefreshToken, jwtProperties.getAccessExpInMs());
    }

    @Override
    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) getSigningKey()).build().parseSignedClaims(token);
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException ex) {
            logger.error("Invalid JWT token. {}", ex.getMessage());
            throw new InvalidJwtTokenException("Invalid JWT token", ex);
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
            throw new InvalidJwtTokenException("JWT claims string is empty", ex);
        }
    }

    @Override
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(storedToken -> {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            logger.info("Token revoked for user '{}'", extractUsername(token));
        });
    }

    @Override
    public void revokeAllTokensForUser(String username) {
        List<RefreshToken> activeTokens = refreshTokenRepository.findActiveTokensByUserId(username);
        if (activeTokens.isEmpty()) {
            logger.error("No active tokens found for user '{}'", username);
            return;
        }

        activeTokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(activeTokens);

        logger.info("Revoked {} token(s) for user '{}'", activeTokens.size(), username);
    }

    @Override
    public String extractToken(HttpServletRequest httpRequest) {
        String authorizationHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }

        return null;
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // **************** Helper Methods **************** //

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        return claimResolver.apply(extractClaims(token));
    }

    private Claims extractClaims(String token) {
        SecretKey key = (SecretKey) getSigningKey();
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    private String buildAccessToken(UserDetails userDetails) {
        return buildToken(
                userDetails,
                jwtProperties.getAccessExpInMs(),
                Map.of("roles", userDetails.getAuthorities())
        );
    }

    private String buildRefreshToken(UserDetails userDetails) {
        return buildToken(
                userDetails,
                jwtProperties.getRefreshExpInMs(),
                null
        );
    }

    private String buildToken(UserDetails userDetails, long expiration, Map<String, Object> claims) {

        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + expiration);

        JwtBuilder builder = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(issuedAt)
                .expiration(expiredAt)
                .signWith(getSigningKey());

        if (claims != null && !claims.isEmpty()) {
            builder.claims(claims);
        }

        return builder.compact();
    }

    private void saveRefreshToken(String token, UserDetails userDetails) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .type(TokenType.BEARER)
                .revoked(false)
                .user((User) userDetails)
                .build();

        refreshTokenRepository.save(refreshToken);
        logger.info("Refresh token saved for the user '{}'", userDetails.getUsername());
    }

    private User getUser(String token) {
        return (User) userDetailsService.loadUserByUsername(extractUsername(token));
    }

}
