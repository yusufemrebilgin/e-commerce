package com.example.ecommerce.payload.response;

import com.example.ecommerce.model.embeddable.Area;
import com.example.ecommerce.model.embeddable.Location;

public record AddressResponse(
        Long id,
        String title,
        Area area,
        Location location
) {}
