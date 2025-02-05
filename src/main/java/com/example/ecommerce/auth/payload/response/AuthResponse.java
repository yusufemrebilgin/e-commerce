package com.example.ecommerce.auth.payload.response;

import java.util.List;

public record AuthResponse(
        String token,
        int expiresIn,
        List<String> roles
) {}
