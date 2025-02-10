package com.example.ecommerce.auth.controller;

import com.example.ecommerce.auth.payload.request.LoginRequest;
import com.example.ecommerce.auth.payload.request.RefreshTokenRequest;
import com.example.ecommerce.auth.payload.request.RegistrationRequest;
import com.example.ecommerce.auth.payload.response.TokenResponse;
import com.example.ecommerce.auth.service.AuthService;
import com.example.ecommerce.auth.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication-related HTTP requests such as login, registration,
 * token refresh, and logout operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    /**
     * Authenticates a user and returns an authentication token pair.
     *
     * @param request the {@link LoginRequest} containing login credentials
     * @return a {@link ResponseEntity} containing the {@link TokenResponse} with the token pair
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Registers a new user in the system and returns an authentication token pair.
     * <p>
     * Only users with {@code ROLE_SUPER_ADMIN} can assign the {@code ROLE_ADMIN} or {@code ROLE_SUPER_ADMIN} roles.
     * If no role is specified or if the role is blank, the user will be assigned the default {@code ROLE_USER}.
     *
     * @param request the {@link RegistrationRequest} containing user registration details with an optional role
     * @return a {@link ResponseEntity} containing the {@link TokenResponse} with the token pair
     */
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * Refreshes the authentication token pair using the provided refresh token.
     *
     * @param request the {@link RefreshTokenRequest} containing the refresh token
     * @return a {@link ResponseEntity} containing the refreshed {@link TokenResponse}
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(tokenService.refreshTokenPair(request.refreshToken()));
    }

    /**
     * Logs out the user by revoking their refresh token and clearing the security context.
     *
     * @param request the {@link RefreshTokenRequest} containing the refresh token
     * @return a {@link ResponseEntity} with no content to indicate successful logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    /**
     * Logs out the user from all active sessions by revoking all refresh tokens associated with their username.
     *
     * @param authenticatedUserDetails the authenticated user's details used to retrieve the username
     * @return a {@link ResponseEntity} with no content to indicate successful logout from all sessions
     */
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal UserDetails authenticatedUserDetails) {
        authService.logoutAll(authenticatedUserDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
