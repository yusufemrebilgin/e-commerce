package com.example.ecommerce;

import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.enums.RoleName;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class ECommerceApplication {

	private final static Logger logger = LoggerFactory.getLogger(ECommerceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ECommerceApplication.class, args);
	}

	@Bean
	CommandLineRunner initUsersData(UserRepository userRepository,
								   RoleRepository roleRepository,
								   PasswordEncoder passwordEncoder) {
		return args -> {
			Role userRole = roleRepository.save(new Role(0L, RoleName.ROLE_USER));
			Role adminRole = roleRepository.save(new Role(0L, RoleName.ROLE_ADMIN));

			User user = User.builder()
					.name("Test User")
					.username("user")
					.password(passwordEncoder.encode("123456"))
					.email("user@example.com")
					.roles(Set.of(userRole))
					.build();

			User admin = User.builder()
					.name("Test Admin")
					.username("admin")
					.password(passwordEncoder.encode("123456"))
					.email("admin@example.com")
					.roles(Set.of(userRole, adminRole))
					.build();

			logger.info("User {}", userRepository.save(user));
			logger.info("User {}", userRepository.save(admin));
		};
	}

}
