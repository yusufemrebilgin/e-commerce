package com.example.ecommerce.auth.service;

import com.example.ecommerce.auth.exception.EmailAlreadyInUseException;
import com.example.ecommerce.auth.exception.ForbiddenRoleAssignmentException;
import com.example.ecommerce.auth.exception.UsernameAlreadyTakenException;
import com.example.ecommerce.auth.payload.request.LoginRequest;
import com.example.ecommerce.auth.payload.request.RegistrationRequest;
import com.example.ecommerce.auth.payload.response.TokenResponse;
import org.springframework.security.core.AuthenticationException;

/**
 * Service interface for authentication-related operations such as login, registration, and logout.
 * This service handles user authentication, role-based access control, and token management.
 * <p>
 * It provides methods for logging in users, registering new users with role validation, and managing user sessions
 * by issuing and revoking tokens.
 */
public interface AuthService {

    /**
     * Authenticates a user and returns an authentication response.
     *
     * @param loginRequest the {@link LoginRequest} containing login credentials
     * @return a {@link TokenResponse} containing the generated token pair for the authenticated user
     * @throws AuthenticationException if authentication fails due to invalid credentials
     */
    TokenResponse login(LoginRequest loginRequest);

    /**
     * Registers a new user and assigns them a role based on the provided registration request.
     * If the role is `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`, the request is validated to ensure that only a super admin
     * can assign such roles.
     *
     * @param registrationRequest the {@link RegistrationRequest} containing user details and role
     * @return a {@link TokenResponse} containing the generated token pair for the newly registered user
     * @throws UsernameAlreadyTakenException    if the username already exists in the database
     * @throws EmailAlreadyInUseException       if the email address is already registered
     * @throws ForbiddenRoleAssignmentException if the user attempting to assign a restricted role (e.g., admin)
     */
    TokenResponse register(RegistrationRequest registrationRequest);

    /**
     * Logs out the user by revoking their refresh token, adding their access token to the blacklist,
     * and clearing the security context.
     *
     * @param authorizationHeader the authorization header containing the access token
     * @param refreshToken the refresh token used to invalidate the session
     */
    void logout(String authorizationHeader, String refreshToken);

    /**
     * Logs out the user by revoking all tokens associated with their username,
     * adding their access tokens to the blacklist, and clearing the security context.
     *
     * @param authorizationHeader the authorization header containing the access token
     * @param authenticatedUsername the username of the user whose tokens are to be revoked
     */
    void logoutAll(String authorizationHeader, String authenticatedUsername);

}
