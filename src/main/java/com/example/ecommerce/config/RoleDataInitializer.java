package com.example.ecommerce.config;

import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.enums.RoleName;
import com.example.ecommerce.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

import static com.example.ecommerce.model.enums.RoleName.*;

@Configuration
public class RoleDataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository repository) {
        return args -> {

            Set<RoleName> roleNames = Set.of(ROLE_ADMIN, ROLE_SELLER, ROLE_CUSTOMER);

            for (RoleName roleName : roleNames) {
                if (repository.findByRoleName(roleName).isEmpty()) {
                    repository.save(new Role(0L, roleName));
                }
            }

        };
    }

}
