package com.example.ecommerce.model.enums;

import java.util.HashSet;
import java.util.Set;

public enum RoleName {

    ROLE_USER,
    ROLE_ADMIN,
    ROLE_SUPER_ADMIN;

    public static RoleName fromName(String name) {
        for (RoleName roleName : values()) {
            String s = roleName.name().substring(5); // Remove ROLE_ prefix
            if (s.equalsIgnoreCase(name)) {
                return roleName;
            }
        }

        throw new IllegalArgumentException("Invalid Role Name: " + name);
    }

    public static Set<RoleName> fromStrings(Set<String> rolesAsStringSet) {
        Set<RoleName> roleNames = new HashSet<>();
        if (rolesAsStringSet != null) {
            for (String role : rolesAsStringSet) {
                roleNames.add(fromName(role));
            }
        }
        return roleNames;
    }

}
