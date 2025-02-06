package com.example.ecommerce.auth.service;

import com.example.ecommerce.auth.exception.AuthenticationRequiredException;
import com.example.ecommerce.auth.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Service interface for retrieving authentication-related user information.
 * <p>
 * This service provides methods to access the currently authenticated user,
 * retrieve their username, and check authentication status. It interacts with
 * Spring Security's {@link SecurityContextHolder} to extract authentication details.
 */
public interface UserContextService {

    /**
     * Returns the currently authenticated user.
     *
     * @return the authenticated {@link User}
     * @throws AuthenticationRequiredException if no user is authenticated
     */
    User getCurrentUser();

    /**
     * Returns the username of the authenticated user.
     *
     * @return the username
     * @throws AuthenticationRequiredException if no user is authenticated
     */
    String getCurrentUsername();

    /**
     * Checks if a user is authenticated.
     *
     * @return {@code true} if authenticated, otherwise {@code false}
     */
    boolean isAuthenticated();

}
