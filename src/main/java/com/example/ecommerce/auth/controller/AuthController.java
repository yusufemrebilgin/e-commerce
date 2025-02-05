package com.example.ecommerce.auth.controller;

import com.example.ecommerce.auth.payload.request.LoginRequest;
import com.example.ecommerce.auth.payload.request.UserRegistrationRequest;
import com.example.ecommerce.auth.payload.response.AuthResponse;
import com.example.ecommerce.shared.payload.MessageResponse;
import com.example.ecommerce.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user and returns an authentication response.
     *
     * @param loginRequest the {@link LoginRequest} containing login credentials
     * @return a {@link ResponseEntity} containing the {@link AuthResponse} with authentication details
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    /**
     * Registers a new user and returns a response message.
     *
     * @param registrationRequest the {@link UserRegistrationRequest} containing user registration details
     * @return a {@link ResponseEntity} containing a {@link MessageResponse} indicating success
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        return ResponseEntity.ok(authService.register(registrationRequest));
    }

}
