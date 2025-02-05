package com.example.ecommerce.address.payload.response;

import com.example.ecommerce.address.model.embeddable.Area;
import com.example.ecommerce.address.model.embeddable.Location;

public record AddressResponse(
        Long id,
        String title,
        Area area,
        Location location
) {}
