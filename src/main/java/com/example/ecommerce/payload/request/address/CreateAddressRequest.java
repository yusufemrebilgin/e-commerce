package com.example.ecommerce.payload.request.address;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateAddressRequest(

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Neighbourhood info is required")
        String neighbourhood,

        @NotBlank(message = "Street info is required")
        String street,

        @NotBlank(message = "Building info is required")
        String building,

        @NotBlank(message = "City info is required")
        String city,

        @NotBlank(message = "District info is required")
        String district,

        @NotBlank(message = "Postal code is required")
        String postalCode,

        String addressDetails

) {}