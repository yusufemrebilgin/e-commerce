package com.example.ecommerce.util;

import com.example.ecommerce.model.User;
import com.example.ecommerce.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class AuthUtils {

    private final CustomUserDetailsService userDetailsService;

    public User getCurrentUser() {
        return fetchAuthenticatedUser();
    }

    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    private User fetchAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        String username = null;
        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }

        return userDetailsService.getUserByUsername(username);
    }

}
