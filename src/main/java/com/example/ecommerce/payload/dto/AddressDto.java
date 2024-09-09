package com.example.ecommerce.payload.dto;

public record AddressDto(
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

}
