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
            loadSuperAdminAccount();
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

    private void loadSuperAdminAccount() {
        final String username = "admin";
        final String password = "adminpw";
        if (!userRepository.existsByUsername(username)) {
            Role role = roleRepository.findByRoleName(ROLE_SUPER_ADMIN)
                    .orElseThrow(() -> new RoleNotFoundException(ROLE_SUPER_ADMIN.name()));

            User admin = User.builder()
                    .name(username)
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(username.concat("@example.com"))
                    .roles(Set.of(role))
                    .build();

            userRepository.save(admin);
            logger.info("Super administrator account created with [u: {}, p: {}]", username, password);
            logger.info("Note: This account created for testing purposes only. Please change it later.");
        } else {
            logger.info("Super administrator account already exists for testing purposes");
        }
    }

}
