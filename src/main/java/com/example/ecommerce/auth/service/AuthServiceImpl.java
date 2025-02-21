package com.example.ecommerce.auth.service;

import com.example.ecommerce.auth.exception.EmailAlreadyInUseException;
import com.example.ecommerce.auth.exception.ForbiddenRoleAssignmentException;
import com.example.ecommerce.auth.exception.UsernameAlreadyTakenException;
import com.example.ecommerce.auth.model.User;
import com.example.ecommerce.auth.model.enums.Role;
import com.example.ecommerce.auth.payload.request.LoginRequest;
import com.example.ecommerce.auth.payload.request.RegistrationRequest;
import com.example.ecommerce.auth.payload.response.TokenResponse;
import com.example.ecommerce.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.ecommerce.auth.model.enums.Role.ROLE_ADMIN;
import static com.example.ecommerce.auth.model.enums.Role.ROLE_SUPER_ADMIN;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final TokenService tokenService;
    private final TokenBlacklistService tokenBlacklistService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        var authToken = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return tokenService.generateTokenPair(userDetails);
    }

    @Override
    @Transactional
    public TokenResponse register(RegistrationRequest registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.username())) {
            throw new UsernameAlreadyTakenException();
        }

        if (userRepository.existsByEmail(registrationRequest.email())) {
            throw new EmailAlreadyInUseException();
        }

        Role givenRole;

        String givenRoleString = registrationRequest.role();
        if (givenRoleString == null || givenRoleString.isBlank()) {
            givenRole = Role.defaultRole();
        } else {
            givenRole = Role.fromName(registrationRequest.role());
        }

        User newUser = User.builder()
                .name(registrationRequest.name())
                .username(registrationRequest.username())
                .password(passwordEncoder.encode(registrationRequest.password()))
                .email(registrationRequest.email())
                .build();

        if (givenRole.equals(ROLE_ADMIN) || givenRole.equals(ROLE_SUPER_ADMIN)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            UserDetails currentUserDetails = Optional.ofNullable(auth)
                    .map(Authentication::getPrincipal)
                    .filter(principal -> principal instanceof UserDetails)
                    .map(principal -> (UserDetails) principal)
                    .orElseThrow(() -> {
                        logger.error("Authentication is missing or invalid while attempting role assignment");
                        return new ForbiddenRoleAssignmentException("Authentication is missing or invalid");
                    });

            boolean isCurrentUserSuperAdmin = currentUserDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_SUPER_ADMIN.name()));

            if (!isCurrentUserSuperAdmin) {
                logger.error("Unauthorized role assignment attempt. User '{}' does not have SUPER_ADMIN authority.", currentUserDetails.getUsername());
                throw new ForbiddenRoleAssignmentException("Only super admins can assign admin or super admin roles.");
            }
        }

        newUser.setRole(givenRole);
        newUser = userRepository.save(newUser);
        logger.info("'{}' registered successfully", newUser.getUsername());

        return tokenService.generateTokenPair(newUser);
    }

    @Override
    public void logout(String authorizationHeader, String refreshToken) {
        tokenService.revokeToken(refreshToken);
        extractAndBlacklistTokenFromGivenHeader(authorizationHeader);
        SecurityContextHolder.clearContext();
    }

    @Override
    public void logoutAll(String authorizationHeader, String authenticatedUsername) {
        tokenService.revokeAllTokensForUser(authenticatedUsername);
        extractAndBlacklistTokenFromGivenHeader(authorizationHeader);
        SecurityContextHolder.clearContext();
    }

    private void extractAndBlacklistTokenFromGivenHeader(String authorizationHeader) {
        if (authorizationHeader == null  || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException();
        }

        String token = authorizationHeader.replace("Bearer ", "");
        long expirationInMs = tokenService.extractExpiration(token).getTime();
        tokenBlacklistService.blacklistToken(token, expirationInMs, TimeUnit.MILLISECONDS);
    }

}
