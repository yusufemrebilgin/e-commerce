package com.example.ecommerce.address.payload.request;

import jakarta.validation.constraints.NotBlank;

/**
 * A request record to create a new address.
 */
public record CreateAddressRequest(

        @NotBlank(message = "Address title is required. Please provide a title for the address.")
        String title,

        @NotBlank(message = "Neighbourhood information is required. Please provide the neighbourhood.")
        String neighbourhood,

        @NotBlank(message = "Street information is required. Please provide the street name or number.")
        String street,

        @NotBlank(message = "Building information is required. Please provide the building name or number.")
        String building,

        @NotBlank(message = "City is required. Please provide the city.")
        String city,

        @NotBlank(message = "District is required. Please provide the district.")
        String district,

        @NotBlank(message = "Postal code is required. Please provide a valid postal code.")
        String postalCode,

        String addressDetails // optional

) {}