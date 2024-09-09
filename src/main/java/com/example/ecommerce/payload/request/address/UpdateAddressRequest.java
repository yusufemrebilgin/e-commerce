package com.example.ecommerce.payload.request.address;

public record UpdateAddressRequest(
        String title,
        String neighbourhood,
        String street,
        String building,
        String city,
        String district,
        String postalCode,
        String addressDetails
) {}
