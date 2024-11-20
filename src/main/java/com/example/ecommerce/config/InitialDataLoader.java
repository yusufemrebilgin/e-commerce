package com.example.ecommerce.config;

import com.example.ecommerce.exception.auth.RoleNotFoundException;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.enums.RoleName;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static com.example.ecommerce.model.enums.RoleName.*;

@Configuration
@RequiredArgsConstructor
public class InitialDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(InitialDataLoader.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner loadInitialData() {
        return args -> {
            loadDefaultRoles();
            loadUserAccount("Super Administrator", "admin", "adminpw", ROLE_SUPER_ADMIN);
            loadUserAccount("Test User", "testuser", "userpw", ROLE_USER);
        };
    }

    private void loadDefaultRoles() {
        Set<RoleName> roleNames = Set.of(ROLE_USER, ROLE_ADMIN, ROLE_SUPER_ADMIN);
        for (RoleName roleName : roleNames) {
            if (roleRepository.findByRoleName(roleName).isEmpty()) {
                roleRepository.save(new Role(0L, roleName));
                logger.info("{} created", roleName);
            }
        }
    }

    private void loadUserAccount(String title, String username, String password, RoleName roleName) {
        if (userRepository.existsByUsername(username)) {
            logger.info("{} account already exists for testing purposes", title);
            return;
        }

        // Getting role for the test account
        Role r = roleRepository.findByRoleName(roleName).orElseThrow(() -> new RoleNotFoundException(roleName.name()));

        User testUser = User.builder()
                .name(username)
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(username.concat("@example.com"))
                .roles(Set.of(r))
                .build();

        userRepository.save(testUser);
        logger.info("'{}' account created with [u: {}, p: {}]", title, username, password);
        logger.warn("Note: This account created for testing purposes only. Please change it later.");
    }

}
