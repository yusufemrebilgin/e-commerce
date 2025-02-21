package com.example.ecommerce.auth.service;

import com.example.ecommerce.auth.exception.EmailAlreadyInUseException;
import com.example.ecommerce.auth.exception.UsernameAlreadyTakenException;
import com.example.ecommerce.auth.model.User;
import com.example.ecommerce.auth.payload.request.LoginRequest;
import com.example.ecommerce.auth.payload.request.RegistrationRequest;
import com.example.ecommerce.auth.payload.response.TokenResponse;
import com.example.ecommerce.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    AuthServiceImpl authService;

    @Mock
    TokenService tokenService;

    @Mock
    TokenBlacklistService tokenBlacklistService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Test
    void givenLoginRequest_whenAttemptToLogin_thenReturnGeneratedTokenResponse() {
        // given
        LoginRequest request = new LoginRequest("test-user", "test-pw");
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        TokenResponse expected = new TokenResponse("access-token", "refresh-token", 6000);

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(auth);
        given(auth.getPrincipal()).willReturn(userDetails);
        given(tokenService.generateTokenPair(userDetails)).willReturn(expected);

        // when
        TokenResponse actual = authService.login(request);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(auth);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(1)).generateTokenPair(userDetails);
    }

    @Test
    void givenValidRegistrationRequest_whenUserRegister_thenReturnGeneratedTokenResponse() {
        // given
        RegistrationRequest request = RegistrationRequest.builder()
                .username("test-user")
                .password("test-pw")
                .build();

        User newUser = User.builder()
                .username(request.username())
                .password(request.password())
                .build();


        TokenResponse expected = new TokenResponse("access-token", "refresh-token", 6000);

        given(passwordEncoder.encode(anyString())).willReturn("encoded-pw");
        given(userRepository.save(any(User.class))).willReturn(newUser);
        given(tokenService.generateTokenPair(newUser)).willReturn(expected);

        // when
        TokenResponse actual = authService.register(request);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenService, times(1)).generateTokenPair(newUser);
    }

    @Test
    void givenRegistrationRequestWithExistingUsername_whenUserAttemptToRegister_thenThrowUsernameAlreadyTakenException() {
        // given
        RegistrationRequest request = RegistrationRequest.builder().username("test-user").build();
        given(userRepository.existsByUsername(anyString())).willReturn(true);

        // when & then
        UsernameAlreadyTakenException ex = catchThrowableOfType(
                UsernameAlreadyTakenException.class,
                () -> authService.register(request)
        );

        assertThat(ex).isNotNull();
        assertThat(ex).isExactlyInstanceOf(UsernameAlreadyTakenException.class);
    }

    @Test
    void givenRegistrationRequestWithExistingEmail_whenUserAttemptToRegister_thenThrowEmailAlreadyInUseException() {
        // given
        RegistrationRequest request = RegistrationRequest.builder().email("test@example.com").build();
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        EmailAlreadyInUseException ex = catchThrowableOfType(
                EmailAlreadyInUseException.class,
                () -> authService.register(request)
        );

        assertThat(ex).isNotNull();
        assertThat(ex).isExactlyInstanceOf(EmailAlreadyInUseException.class);
    }

    @Test
    void givenRefreshToken_whenUserLogout_thenRevokeGivenTokenAndClearSecurityContext() {
        // given
        String refreshToken = "test-refresh-token";
        String authorizationHeader = "test-authorization-header";
        given(tokenService.extractExpiration(anyString())).willReturn(new Date());

        // when & then
        authService.logout(authorizationHeader, refreshToken);

        verify(tokenService).revokeToken(refreshToken);
        verify(tokenBlacklistService).blacklistToken(anyString(), anyLong(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void whenUserLogoutAll_thenRevokeAllTokensAndClearSecurityContext() {
        // given
        User authenticatedUser = User.builder()
                .username("test-user")
                .password("test-pw")
                .build();

        String authorizationHeader = "test-authorization-header";

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(authenticatedUser, null)
        );

        given(tokenService.extractExpiration(anyString())).willReturn(new Date());

        // when & then
        authService.logoutAll(authorizationHeader, authenticatedUser.getUsername());

        verify(tokenService).revokeAllTokensForUser(authenticatedUser.getUsername());
        verify(tokenBlacklistService).blacklistToken(anyString(), anyLong(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

}