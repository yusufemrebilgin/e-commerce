package com.example.ecommerce.payload.dto;

import lombok.Builder;

@Builder
public record ProductImageDto(String filename, String url) {}
