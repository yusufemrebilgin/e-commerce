package com.example.ecommerce.model.enums;

import java.util.HashSet;
import java.util.Set;

public enum RoleName {

    ROLE_USER,
    ROLE_ADMIN,
    ROLE_SUPER_ADMIN;

    /**
     * Converts a string to the corresponding {@link RoleName} enum constant by removing
     * the {@code ROLE_} prefix and performing a case-insensitive match.
     *
     * @param name the string representation of the role without the {@code  ROLE} prefix
     * @return the {@link RoleName} corresponding to the given name
     * @throws IllegalArgumentException if the provided role name is invalid or not supported
     */
    public static RoleName fromName(String name) {
        for (RoleName roleName : values()) {
            String s = roleName.name().substring(5); // Remove ROLE_ prefix
            if (s.equalsIgnoreCase(name)) {
                return roleName;
            }
        }

        throw new IllegalArgumentException("Invalid Role Name: " + name);
    }

    /**
     * Converts a set of role names represented as strings to a set of {@link RoleName} enum
     * constants.
     *
     * @param rolesAsStringSet a set of strings representing role names without the {@code ROLE_} prefix
     * @return a set of {@link RoleName} corresponding to the given strings
     */
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
