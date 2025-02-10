package com.example.ecommerce.shared.config;

import com.example.ecommerce.auth.model.User;
import com.example.ecommerce.auth.model.enums.Role;
import com.example.ecommerce.auth.repository.UserRepository;
import com.example.ecommerce.shared.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.ecommerce.auth.model.enums.Role.ROLE_SUPER_ADMIN;
import static com.example.ecommerce.auth.model.enums.Role.ROLE_USER;


@Configuration
@RequiredArgsConstructor
public class InitialDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(InitialDataLoader.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner loadInitialData() {
        return args -> SecurityUtils.runWithTemporarySystemAuthentication(() -> {
            loadUserAccount("Admin", "admin", "adminpw", ROLE_SUPER_ADMIN);
            loadUserAccount("User", "user", "userpw", ROLE_USER);
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

}
