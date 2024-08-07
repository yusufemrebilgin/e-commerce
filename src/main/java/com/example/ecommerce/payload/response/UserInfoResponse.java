package com.example.ecommerce.payload.response;

import java.util.List;

public record UserInfoResponse(Long id, String jwtToken, String username, List<String> roles) {
}
