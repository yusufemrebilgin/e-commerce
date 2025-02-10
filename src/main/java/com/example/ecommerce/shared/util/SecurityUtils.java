package com.example.ecommerce.shared.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * Utility class for handling temporary authentication context.
 * This is useful when performing actions that require authentication (e.g., auditing)
 * but are executed in a non-authenticated context.
 */
@UtilityClass
public class SecurityUtils {

    /**
     * Runs a given task with a temporary system authentication context.
     * This ensures that security-sensitive operations (like entity auditing)
     * can be performed without requiring an actual user login.
     *
     * @param task The runnable task to execute with system authentication.
     */
    public static void runWithTemporarySystemAuthentication(Runnable task) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("system", null, List.of()));
        SecurityContextHolder.setContext(context);

        try {
            task.run();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

}
