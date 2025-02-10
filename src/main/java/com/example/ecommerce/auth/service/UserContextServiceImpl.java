package com.example.ecommerce.auth.service;

import com.example.ecommerce.auth.exception.AuthenticationRequiredException;
import com.example.ecommerce.auth.model.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserContextServiceImpl implements UserContextService {

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationRequiredException("User is not authenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        throw new AuthenticationRequiredException("Invalid authentication principal");
    }

    @Override
    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    @Override
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated();
    }

}
