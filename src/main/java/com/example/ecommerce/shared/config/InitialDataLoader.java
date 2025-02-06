package com.example.ecommerce.shared.config;

import com.example.ecommerce.auth.model.User;
import com.example.ecommerce.auth.model.enums.Role;
import com.example.ecommerce.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static com.example.ecommerce.auth.model.enums.Role.ROLE_SUPER_ADMIN;
import static com.example.ecommerce.auth.model.enums.Role.ROLE_USER;

/**
 * Loads initial test user accounts into the database on application startup.
 * <p>
 * This class ensures that predefined test users are available for authentication.
 * It temporarily sets a system authentication context to allow auditing fields
 * such as {@code createdBy} to be populated correctly.
 */
@Configuration
@RequiredArgsConstructor
public class InitialDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(InitialDataLoader.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner loadInitialData() {
        return args -> runWithTemporarySystemAuthentication(() -> {
            loadUserAccount("Admin", "default_admin", "adminpw", ROLE_SUPER_ADMIN);
            loadUserAccount("User", "default_user", "userpw", ROLE_USER);
        });
    }

    private void loadUserAccount(String title, String username, String password, Role role) {
        if (userRepository.existsByUsername(username)) {
            logger.info("{} account already exists for testing purposes", title);
            return;
        }

        User testUser = User.builder()
                .name(title)
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(username.concat("@example.com"))
                .role(role)
                .build();

        userRepository.save(testUser);

        logger.warn("""
        \n
            ** {} account created with [username: '{}', password: '{}', role: '{}']
            ** Note: This account created for testing purposes only. Please change it later.
        
        """, title, username, password, role.name());
    }

    private void runWithTemporarySystemAuthentication(Runnable task) {
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
