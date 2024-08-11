package com.example.ecommerce.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public final class URIBuilder {

    public static URI getResourceLocation() {
        return ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
    }

    public static URI getResourceLocation(Object id) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(id).toUri();
    }

}
