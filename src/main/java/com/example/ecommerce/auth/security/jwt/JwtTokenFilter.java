package com.example.ecommerce.auth.security.jwt;

import com.example.ecommerce.auth.exception.TokenRevokedException;
import com.example.ecommerce.auth.service.TokenBlacklistService;
import com.example.ecommerce.auth.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter intercepts incoming HTTP requests to extract and validate JWT tokens,
 * authenticates the user based on the token, and sets the authentication context.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final TokenService tokenService;
    private final TokenBlacklistService tokenBlacklistService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        logger.debug("Filtering request for URI: {}", request.getRequestURI());

        String jwtToken = tokenService.extractToken(request);
        if (!StringUtils.hasText(jwtToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (tokenBlacklistService.isTokenBlacklisted(jwtToken)) {
            throw new TokenRevokedException();
        }

        try {
            tokenService.validateToken(jwtToken);
            authenticateRequest(jwtToken, request);
        } catch (Exception ex) {
            logger.error("Cannot set user authentication. {}", ex.getMessage());
            throw ex;
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    private void authenticateRequest(String jwtToken, HttpServletRequest request) {
        String username = tokenService.extractUsername(jwtToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
