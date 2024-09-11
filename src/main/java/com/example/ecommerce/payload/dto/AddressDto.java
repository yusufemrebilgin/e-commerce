package com.example.ecommerce.payload.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AddressDto(
        Long id,
        String title,
        Area area,
        Location location
) {

    public record Area(
            String city,
            String district,
            String postalCode
    ) {}

    public record Location(
            String neighbourhood,
            String street,
            String building,
            String details
    ) {}

    public AddressDto() {
        this(0L, null, null, null);
    }

}
