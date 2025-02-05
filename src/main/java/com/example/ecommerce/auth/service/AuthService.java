package com.example.ecommerce.auth.service;

import com.example.ecommerce.auth.exception.EmailAlreadyInUseException;
import com.example.ecommerce.auth.exception.ForbiddenRoleAssignmentException;
import com.example.ecommerce.auth.exception.UsernameAlreadyTakenException;
import com.example.ecommerce.auth.model.Role;
import com.example.ecommerce.auth.model.User;
import com.example.ecommerce.auth.model.enums.RoleName;
import com.example.ecommerce.auth.payload.request.LoginRequest;
import com.example.ecommerce.auth.payload.request.UserRegistrationRequest;
import com.example.ecommerce.auth.payload.response.AuthResponse;
import com.example.ecommerce.shared.payload.MessageResponse;
import com.example.ecommerce.auth.repository.UserRepository;
import com.example.ecommerce.auth.security.CustomUserDetails;
import com.example.ecommerce.auth.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.ecommerce.auth.model.enums.RoleName.ROLE_ADMIN;
import static com.example.ecommerce.auth.model.enums.RoleName.ROLE_SUPER_ADMIN;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final RoleService roleService;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param loginRequest request containing username and password
     * @return {@link AuthResponse} containing JWT token and user roles
     */
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

    /**
     * Register a new user in the system.
     *
     * @param userRegistrationRequest request containing user registration details
     * @return {@link MessageResponse} indicating successful registration
     * @throws UsernameAlreadyTakenException    if username is already taken
     * @throws EmailAlreadyInUseException       if email is already in use
     * @throws ForbiddenRoleAssignmentException if user attempts to assign a forbidden role
     */
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

    /**
     * Retrieves a user by their username.
     *
     * @param username username of the user to find
     * @return found {@link User}
     * @throws UsernameNotFoundException if user is not found by username
     */
    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Retrieves currently authenticated user.
     *
     * @return {@link User} object of the authenticated user
     * @throws IllegalStateException     if user is not authenticated
     * @throws UsernameNotFoundException if user is not found by specified username
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        String username = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }

        return findUserByUsername(username);
    }

    /**
     * Retrieves username of the currently authenticated user.
     *
     * @return username of the current user
     * @throws IllegalStateException     if user is not authenticated
     * @throws UsernameNotFoundException if user is not found by specified username
     */
    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

}
