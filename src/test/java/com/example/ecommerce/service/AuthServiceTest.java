package com.example.ecommerce.service;

import com.example.ecommerce.constant.ErrorMessages;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
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

    @Test
    void givenValidLoginRequest_whenSuccess_returnAuthResponse() {
        // given
        LoginRequest request = new LoginRequest("username", "password");

        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        AuthResponse expected = new AuthResponse(
                "jwtToken",
                3600,
                List.of()
        );

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(userDetails.getAuthorities()).willReturn(Collections.emptyList());
        given(jwtUtils.generateTokenFromUsername(userDetails)).willReturn(Map.of("token", "jwtToken", "expiresIn", 3600));

        // when
        AuthResponse actual = authService.login(request);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual.expiresIn()).isEqualTo(3600);
        then(actual.token()).isEqualTo("jwtToken");
        then(actual.roles()).isEmpty();
    }

    @Test
    void givenValidUserRegistrationRequest_whenSuccess_returnMessageResponse() {
        // given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "Test User",
                "test_username",
                "test_password",
                "test@example.com",
                Set.of("user")
        );

        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(request.password())).willReturn("encoded_password");
        given(roleService.assignRoles(anySet())).willReturn(Set.of(new Role(0L, RoleName.ROLE_USER)));

        // when
        MessageResponse response = authService.register(request);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        User user = userArgumentCaptor.getValue();

        then(user.getUsername()).isEqualTo("test_username");
        then(user.getPassword()).isEqualTo("encoded_password");
        then(user.getEmail()).isEqualTo("test@example.com");
        then(user.getRoles()).hasSize(1);
        then(user.getRoles().stream().map(Role::getRoleName).toList()).contains(RoleName.ROLE_USER);

        then(response).isNotNull();
        then(response.message()).isEqualTo("User registered successfully!");
    }

    @Test
    void givenUserRegistrationRequestWithExistingUsername_whenUsernameFound_throwUsernameAlreadyTakenException() {
        // given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "test", "username", "password", "@example.com", Set.of()
        );

        given(userRepository.existsByUsername(anyString())).willReturn(true);

        // when
        UsernameAlreadyTakenException ex = catchThrowableOfType(
                () -> authService.register(request),
                UsernameAlreadyTakenException.class
        );

        // then
        then(ex).isNotNull();
        then(ex).hasMessageContaining(ErrorMessages.USERNAME_ALREADY_TAKEN.message());
    }

    @Test
    void givenUserRegistrationRequestWithExistingEmail_whenEmailFound_throwEmailAlreadyInUseException() {
        // given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "test", "username", "password", "@example.com", Set.of()
        );

        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when
        EmailAlreadyInUseException ex = catchThrowableOfType(
                () -> authService.register(request),
                EmailAlreadyInUseException.class
        );

        // then
        then(ex).isNotNull();
        then(ex).hasMessageContaining("Email is already in use");
    }

    @Test
    void givenRegistrationRequestWithNonAdminAuth_whenTriesToAssignAdminRole_throwUnauthorizedRoleAssignmentException() {
        // given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "test", "testuser", "password", "@example.com", Set.of("admin")
        );
        CustomUserDetails nonAdminDetails = new CustomUserDetails(
                0L, "nonadmin", "@example.com", "password", Set.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(nonAdminDetails, null, nonAdminDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ForbiddenRoleAssignmentException ex = catchThrowableOfType(
                () -> authService.register(request),
                ForbiddenRoleAssignmentException.class
        );

        // then
        then(ex).isNotNull();
        then(ex).hasMessageContaining(ErrorMessages.FORBIDDEN_ROLE_ASSIGNMENT.message());
    }

}
