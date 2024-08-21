package com.example.ecommerce.model.enums;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public enum RoleName {

    ROLE_USER,
    ROLE_ADMIN;

    public static RoleName fromName(String name) {
        return switch (name) {
            case "USER" -> ROLE_USER;
            case "ADMIN" -> ROLE_ADMIN;
            default -> throw new IllegalStateException("Invalid Role Name: " + name);
        };
    }

    public static Set<RoleName> fromStrings(Set<String> rolesAsStringSet) {
        Set<RoleName> roleNames = new HashSet<>();
        if (rolesAsStringSet != null) {
            for (String role : rolesAsStringSet) {
                roleNames.add(fromName(role.toUpperCase(Locale.ENGLISH)));
            }
        }
        return roleNames;
    }

}
