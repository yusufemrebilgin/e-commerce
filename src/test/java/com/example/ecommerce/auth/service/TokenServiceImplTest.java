package com.example.ecommerce.auth.service;

import com.example.ecommerce.auth.config.JwtProperties;
import com.example.ecommerce.auth.exception.InvalidJwtTokenException;
import com.example.ecommerce.auth.exception.TokenRevokedException;
import com.example.ecommerce.auth.model.RefreshToken;
import com.example.ecommerce.auth.model.User;
import com.example.ecommerce.auth.payload.response.TokenResponse;
import com.example.ecommerce.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenServiceImplTest {

    @InjectMocks
    TokenServiceImpl tokenService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    JwtProperties jwtProperties;

    @Mock
    UserDetailsService userDetailsService;

    static final String SECRET_KEY = Encoders.BASE64.encode(
            "MockSecretKeyWithSufficientLengthForHMacSHA256".getBytes()
    );

    @BeforeEach
    void setUp() {
        given(jwtProperties.getSecret()).willReturn(SECRET_KEY);
        given(jwtProperties.getAccessExpInMs()).willReturn(900_000L);
        given(jwtProperties.getRefreshExpInMs()).willReturn(604_800_000L);
    }

    @Test
    void givenUserDetails_whenGenerateTokenPair_thenSaveGeneratedRefreshTokenAndReturnTokenResponse() {
        // given
        UserDetails userDetails = User.builder()
                .username("test-user")
                .password("test-pw")
                .build();

        // when
        TokenResponse response = tokenService.generateTokenPair(userDetails);

        // then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isNotNull();
        assertThat(response.refreshToken()).isNotNull();
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void givenValidRefreshToken_whenRefreshTokenPair_thenRevokeOldTokenAndGenerateNewTokenPair() {
        // given
        User user = User.builder()
                .username("test-user")
                .build();

        String refreshToken = buildValidToken();

        RefreshToken storedOldToken = RefreshToken.builder()
                .token(refreshToken)
                .revoked(false)
                .user(user)
                .build();

        given(userDetailsService.loadUserByUsername(anyString())).willReturn(user);
        given(refreshTokenRepository.findByToken(refreshToken)).willReturn(Optional.of(storedOldToken));

        // when
        TokenResponse response = tokenService.refreshTokenPair(refreshToken);

        // then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isNotNull();
        assertThat(response.refreshToken()).isNotNull();
        assertThat(storedOldToken.isRevoked()).isTrue();
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }

    @Test
    void givenInvalidRefreshToken_whenRefreshTokenPair_thenThrowInvalidJwtTokenException() {
        // given
        String invalidToken = buildExpiredToken();

        // when
        InvalidJwtTokenException ex = catchThrowableOfType(
                InvalidJwtTokenException.class,
                () -> tokenService.refreshTokenPair(invalidToken)
        );

        // then
        assertThat(ex).isNotNull();
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }


    @Test
    void givenRevokedToken_whenRefreshTokenPair_thenThrowTokenRevokedException() {
        // given
        String refreshToken = buildValidToken();

        RefreshToken revokedToken = RefreshToken.builder()
                .token(refreshToken)
                .revoked(true)
                .build();

        given(userDetailsService.loadUserByUsername(anyString())).willReturn(new User());
        given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.of(revokedToken));

        // when
        TokenRevokedException ex = catchThrowableOfType(
                TokenRevokedException.class,
                () -> tokenService.refreshTokenPair(refreshToken)
        );

        // then
        assertThat(ex).isNotNull();
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }


    @Test
    void givenMalformedToken_whenValidateToken_thenThrowInvalidJwtExceptionWithCauseMalformedJwtException() {
        // given
        String token = "invalid.token";

        // when & then
        InvalidJwtTokenException ex = catchThrowableOfType(
                InvalidJwtTokenException.class,
                () -> tokenService.validateToken(token)
        );

        assertThat(ex).isNotNull();
        assertThat(ex.getCause()).isInstanceOf(MalformedJwtException.class);
    }

    @Test
    void givenExpiredToken_whenValidateToken_thenThrowInvalidJwtExceptionWithCauseExpiredJwtException() {
        // given
        String token = buildExpiredToken();

        // when & then
        InvalidJwtTokenException ex = catchThrowableOfType(
                InvalidJwtTokenException.class,
                () -> tokenService.validateToken(token)
        );

        assertThat(ex).isNotNull();
        assertThat(ex.getCause()).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void givenExistingToken_whenRevokeToken_thenRevokeTokenAndSave() {
        // given
        RefreshToken storedToken = RefreshToken.builder()
                .token(buildValidToken())
                .revoked(false)
                .build();

        given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.of(storedToken));

        // when & then
        tokenService.revokeToken(storedToken.getToken());

        assertThat(storedToken.isRevoked()).isTrue();
        verify(refreshTokenRepository, times(1)).save(storedToken);
    }

    @Test
    void givenActiveTokenList_whenRevokeAllTokensForUser_thenRevokeAllTokensAndSave() {
        // given
        String username = "test-username";

        String t1 = buildToken(username, 100_000);
        String t2 = buildToken(username, 200_000);
        String t3 = buildToken(username, 300_000);

        List<RefreshToken> activeTokens = List.of(
                RefreshToken.builder().token(t1).revoked(false).build(),
                RefreshToken.builder().token(t2).revoked(false).build(),
                RefreshToken.builder().token(t3).revoked(false).build()
        );

        given(refreshTokenRepository.findActiveTokensByUserId(anyString())).willReturn(activeTokens);

        // when & then
        tokenService.revokeAllTokensForUser(username);

        activeTokens.forEach(token -> assertThat(token.isRevoked()).isTrue());
        verify(refreshTokenRepository, times(1)).saveAll(activeTokens);
    }

    @Test
    void givenHttpServletRequest_whenExtractToken_thenReturnTokenAsString() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);

        String expectedToken = buildValidToken();
        String authorizationHeader = "Bearer " + expectedToken;

        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(authorizationHeader);

        // when & then
        String actualToken = tokenService.extractToken(request);

        assertThat(actualToken).isNotNull();
        assertThat(actualToken).isEqualTo(expectedToken);
    }

    @Test
    void givenValidToken_whenExtractUsername_thenReturnUsername() {
        // given
        String username = "test-username";
        String validToken = buildToken(username, 6000);

        // when & then
        String extractedUsername = tokenService.extractUsername(validToken);

        assertThat(extractedUsername).isNotNull();
        assertThat(extractedUsername).isEqualTo(username);
    }

    private String buildValidToken() {
        return buildToken("test-user", 60000);
    }

    private String buildExpiredToken() {
        return buildToken("test-user", -60000);
    }

    private String buildToken(String username, long expiration) {

        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expiredAt)
                .signWith(key)
                .compact();
    }

}