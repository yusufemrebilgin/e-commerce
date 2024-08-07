package com.example.ecommerce.payload.response;

import java.util.List;

public record AuthResponse(
        String token,
        int expiresIn,
        List<String> roles
) {}
