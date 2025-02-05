package com.example.ecommerce.service;

import com.example.ecommerce.shared.constant.ErrorMessages;
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
import com.example.ecommerce.auth.service.AuthService;
import com.example.ecommerce.auth.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleService roleService;

    @Mock
    PasswordEncoder passwordEncoder;

    private LoginRequest loginRequest;
    private UserRegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("username", "password");
        registrationRequest = new UserRegistrationRequest(
                "name", "username", "password", "email@example.com", Set.of("USER")
        );
    }

    @Test
    void givenValidLoginRequest_whenAuthenticateUser_thenReturnAuthResponse() {
        // given
        Authentication auth = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        AuthResponse expected = new AuthResponse("jwtToken", 3600, List.of());

        Map<String, Object> jwtResponse = Map.of(
                "token", "jwtToken",
                "expiresIn", 3600
        );

        given(authenticationManager.authenticate(any())).willReturn(auth);
        given(auth.getPrincipal()).willReturn(userDetails);
        given(jwtUtils.generateTokenFromUsername(userDetails)).willReturn(jwtResponse);

        // when
        AuthResponse actual = authService.login(loginRequest);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void givenValidRegistrationRequest_whenRegisterUser_thenReturnSuccessResponse() {
        // given
        given(passwordEncoder.encode(registrationRequest.password())).willReturn("encoded_pw");
        given(roleService.assignRoles(anySet())).willReturn(Set.of(new Role(0L, RoleName.ROLE_USER)));

        // when
        MessageResponse response = authService.register(registrationRequest);

        // then
        then(response).isNotNull();
        then(response.message()).contains("success");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenRegistrationRequestWithExistingUsername_whenRegisterUser_thenThrowUsernameAlreadyTakenException() {
        // given
        given(userRepository.existsByUsername(anyString())).willReturn(true);

        // when & then
        UsernameAlreadyTakenException ex = catchThrowableOfType(
                () -> authService.register(registrationRequest),
                UsernameAlreadyTakenException.class
        );

        then(ex).isNotNull();
        then(ex.getMessage()).isEqualTo(ErrorMessages.USERNAME_ALREADY_TAKEN.message());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenRegistrationRequestWithExistingEmail_whenRegisterUser_thenThrowEmailAlreadyInUseException() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        EmailAlreadyInUseException ex = catchThrowableOfType(
                () -> authService.register(registrationRequest),
                EmailAlreadyInUseException.class
        );

        then(ex).isNotNull();
        then(ex.getMessage()).isEqualTo(ErrorMessages.EMAIL_ALREADY_IN_USE.message());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenAdminRegistrationRequestWithSuperAdminAuth_whenRegisterAdmin_thenReturnSuccessResponse() {
        // given
        UserRegistrationRequest adminRegistrationRequest = UserRegistrationRequest.builder()
                .roles(Set.of("ADMIN"))
                .build();

        User superAdmin = User.builder()
                .roles(Set.of(new Role(0L, RoleName.ROLE_SUPER_ADMIN)))
                .build();

        CustomUserDetails superAdminDetails = CustomUserDetails.build(superAdmin);
        Authentication auth = new UsernamePasswordAuthenticationToken(superAdminDetails, null, superAdminDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // when
        MessageResponse response = authService.register(adminRegistrationRequest);

        // then
        then(response).isNotNull();
        then(response.message()).contains("success");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenAdminRegistrationRequestWithNonSuperAdminAuth_whenRegisterAdmin_thenThrowForbiddenRoleAssignmentException() {
        // given
        UserRegistrationRequest adminRegistrationRequest = UserRegistrationRequest.builder()
                .roles(Set.of("ADMIN"))
                .build();

        User nonAdminUser = User.builder()
                .roles(Set.of(new Role(0L, RoleName.ROLE_USER)))
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(nonAdminUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // when
        ForbiddenRoleAssignmentException ex = catchThrowableOfType(
                () -> authService.register(adminRegistrationRequest),
                ForbiddenRoleAssignmentException.class
        );

        // then
        then(ex).isNotNull();
        then(ex.getMessage()).isEqualTo(ErrorMessages.FORBIDDEN_ROLE_ASSIGNMENT.message());
        verify(userRepository, never()).save(any(User.class));
    }

}
