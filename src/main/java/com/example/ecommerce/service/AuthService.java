package com.example.ecommerce.service;

import com.example.ecommerce.exception.EmailAlreadyInUseException;
import com.example.ecommerce.exception.RoleNotFoundException;
import com.example.ecommerce.exception.UnauthorizedRoleAssignmentException;
import com.example.ecommerce.exception.UsernameAlreadyTakenException;
import com.example.ecommerce.model.Role;
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

        User user = User.builder()
                .name(userRegistrationRequest.name())
                .username(userRegistrationRequest.username())
                .email(userRegistrationRequest.email())
                .password(encoder.encode(userRegistrationRequest.password()))
                .build();

        Set<Role> roles = new HashSet<>();
        Set<RoleName> givenRoles = RoleName.fromStrings(userRegistrationRequest.roles());

        if (givenRoles.contains(RoleName.ROLE_ADMIN)) {
            throw new UnauthorizedRoleAssignmentException("Admin role cannot be assigned");
        }

        if (givenRoles.isEmpty()) {
            Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                    .orElseGet(() -> {
                        // If role is not found create user role as default
                        return roleRepository.save(new Role(0L, RoleName.ROLE_USER));
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
