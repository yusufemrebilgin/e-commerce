package com.example.ecommerce.payload.dto;

import lombok.Builder;

@Builder
public record CategoryDto(Long categoryId, String categoryName) {}
