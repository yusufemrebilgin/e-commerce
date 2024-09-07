package com.example.ecommerce.service;

import com.example.ecommerce.exception.user.EmailAlreadyInUseException;
import com.example.ecommerce.exception.user.RoleNotFoundException;
import com.example.ecommerce.exception.user.ForbiddenRoleAssignmentException;
import com.example.ecommerce.exception.user.UsernameAlreadyTakenException;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.Seller;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.enums.RoleName;
import com.example.ecommerce.payload.request.auth.LoginRequest;
import com.example.ecommerce.payload.request.auth.UserRegistrationRequest;
import com.example.ecommerce.payload.response.AuthResponse;
import com.example.ecommerce.payload.response.MessageResponse;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.CustomUserDetails;
import com.example.ecommerce.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.ecommerce.model.enums.RoleName.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public AuthResponse login(LoginRequest loginRequest) {

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Map<String, Object> jwt = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new AuthResponse(
                (String) jwt.get("token"),
                (Integer) jwt.get("expiresIn"),
                roles
        );
    }

    public MessageResponse register(UserRegistrationRequest userRegistrationRequest) {
        if (userRepository.existsByUsername(userRegistrationRequest.username())) {
            throw new UsernameAlreadyTakenException();
        }

        if (userRepository.existsByEmail(userRegistrationRequest.email())) {
            throw new EmailAlreadyInUseException();
        }

        Set<Role> roles = new HashSet<>();
        Set<RoleName> givenRoles = RoleName.fromStrings(userRegistrationRequest.roles());

        User user;
        if (givenRoles.contains(ROLE_SELLER)) {
            user = new Seller();
        } else if (givenRoles.contains(ROLE_CUSTOMER)) {
            user = new Customer();
        } else {
            user = new User();
        }

        user.setName(userRegistrationRequest.name());
        user.setUsername(userRegistrationRequest.username());
        user.setPassword(encoder.encode(userRegistrationRequest.password()));
        user.setEmail(userRegistrationRequest.email());

        if (givenRoles.contains(ROLE_ADMIN) || givenRoles.contains(ROLE_SUPER_ADMIN)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();

            boolean isSuperAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_SUPER_ADMIN.name()));

            if (!isSuperAdmin) {
                throw new ForbiddenRoleAssignmentException();
            }
        }

        if (givenRoles.isEmpty()) {
            Role userRole = roleRepository.findByRoleName(ROLE_CUSTOMER)
                    .orElseGet(() -> {
                        // If role is not found create customer role as default
                        return roleRepository.save(new Role(0L, ROLE_CUSTOMER));
                    });
            roles.add(userRole);
        } else {
            for (RoleName roleName : givenRoles) {
                Role existingRole = roleRepository.findByRoleName(roleName)
                        .orElseThrow(() -> new RoleNotFoundException(roleName.name()));
                roles.add(existingRole);
            }
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }

}
