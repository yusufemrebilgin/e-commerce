package com.example.ecommerce.auth.service;

import com.example.ecommerce.auth.exception.AuthenticationRequiredException;
import com.example.ecommerce.auth.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserContextServiceImplTest {

    @InjectMocks
    UserContextServiceImpl userContextService;

    @Mock
    SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void givenUserAuthentication_whenGetCurrentUser_thenReturnUser() {
        // given
        User authenticatedUser = User.builder()
                .username("test-user")
                .password("password")
                .build();

        Authentication auth = new TestingAuthenticationToken(authenticatedUser, null);
        auth.setAuthenticated(true);

        given(securityContext.getAuthentication()).willReturn(auth);

        // when
        User result = userContextService.getCurrentUser();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(authenticatedUser.getUsername());
    }

    @Test
    void givenNullAuthentication_whenGetCurrentUser_thenThrowAuthenticationRequiredException() {
        // given
        given(securityContext.getAuthentication()).willReturn(null);

        // when
        AuthenticationRequiredException ex = catchThrowableOfType(
                AuthenticationRequiredException.class,
                userContextService::getCurrentUser
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex).isInstanceOf(AuthenticationRequiredException.class);
    }

    @Test
    void givenAnonymousAuthenticationToken_whenGetCurrentUser_thenThrowAuthenticationRequiredException() {
        // given
        AnonymousAuthenticationToken token = new AnonymousAuthenticationToken(
                "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
        );

        given(securityContext.getAuthentication()).willReturn(token);

        // when
        AuthenticationRequiredException ex = catchThrowableOfType(
                AuthenticationRequiredException.class,
                userContextService::getCurrentUser
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex).isInstanceOf(AuthenticationRequiredException.class);
    }

    @Test
    void givenUndefinedUserAuthentication_whenGetCurrentUser_thenThrowAuthenticationRequiredException() {
        // given
        Authentication auth = new TestingAuthenticationToken("not-a-user", null);
        auth.setAuthenticated(true);

        given(securityContext.getAuthentication()).willReturn(auth);

        // when
        AuthenticationRequiredException ex = catchThrowableOfType(
                AuthenticationRequiredException.class,
                userContextService::getCurrentUser
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex).isInstanceOf(AuthenticationRequiredException.class);
    }

    @Test
    void givenAuthenticatedUser_whenGetCurrentUsername_thenReturnUsername() {
        // given
        User authenticatedUser = User.builder().username("test-user").build();
        Authentication auth = new TestingAuthenticationToken(authenticatedUser, null);
        auth.setAuthenticated(true);

        given(securityContext.getAuthentication()).willReturn(auth);

        // when
        String returnedUsername = userContextService.getCurrentUsername();

        // then
        assertThat(returnedUsername).isEqualTo(authenticatedUser.getUsername());
    }

    @Test
    void givenNullAuthentication_whenGetCurrentUsername_thenThrowAuthenticationRequiredException() {
        // given
        given(securityContext.getAuthentication()).willReturn(null);

        // when
        AuthenticationRequiredException ex = catchThrowableOfType(
                AuthenticationRequiredException.class,
                userContextService::getCurrentUsername
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex).isInstanceOf(AuthenticationRequiredException.class);
    }

    @Test
    void givenAuthenticatedToken_whenIsAuthenticated_thenReturnTrue() {
        // given
        Authentication auth = new TestingAuthenticationToken(null, null);
        auth.setAuthenticated(true);

        given(securityContext.getAuthentication()).willReturn(auth);

        // when
        boolean result = userContextService.isAuthenticated();

        // then
        assertThat(result).isTrue();
    }

    @Test
    void givenUnauthenticatedToken_whenIsAuthenticated_thenReturnFalse() {
        // given
        Authentication auth = new TestingAuthenticationToken(null, null);
        auth.setAuthenticated(false);

        given(securityContext.getAuthentication()).willReturn(auth);

        // when
        boolean result = userContextService.isAuthenticated();

        // then
        assertThat(result).isFalse();
    }

}