package com.example.ecommerce.exception.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SimpleErrorResponse {

    private String status;
    private String message;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

}