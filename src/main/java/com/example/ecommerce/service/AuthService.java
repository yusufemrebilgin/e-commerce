package com.example.ecommerce.service;

import com.example.ecommerce.exception.user.EmailAlreadyInUseException;
import com.example.ecommerce.exception.user.ForbiddenRoleAssignmentException;
import com.example.ecommerce.exception.user.UsernameAlreadyTakenException;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.enums.RoleName;
import com.example.ecommerce.payload.request.auth.LoginRequest;
import com.example.ecommerce.payload.request.auth.UserRegistrationRequest;
import com.example.ecommerce.payload.response.AuthResponse;
import com.example.ecommerce.payload.response.MessageResponse;
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.ecommerce.model.enums.RoleName.ROLE_ADMIN;
import static com.example.ecommerce.model.enums.RoleName.ROLE_SUPER_ADMIN;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final RoleService roleService;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;


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

        Set<RoleName> givenRoles = RoleName.fromStrings(userRegistrationRequest.roles());

        User user = User.builder()
                .name(userRegistrationRequest.name())
                .username(userRegistrationRequest.username())
                .password(encoder.encode(userRegistrationRequest.password()))
                .email(userRegistrationRequest.email())
                .build();

        if (givenRoles.contains(ROLE_ADMIN) || givenRoles.contains(ROLE_SUPER_ADMIN)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();

            boolean isSuperAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_SUPER_ADMIN.name()));

            if (!isSuperAdmin) {
                throw new ForbiddenRoleAssignmentException();
            }
        }

        Set<Role> roles = givenRoles.isEmpty()
                ? roleService.assignDefaultRole()
                : roleService.assignRoles(givenRoles);

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }

}
