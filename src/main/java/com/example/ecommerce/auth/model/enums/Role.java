package com.example.ecommerce.auth.model.enums;

public enum Role {

    ROLE_USER,
    ROLE_ADMIN,
    ROLE_SUPER_ADMIN;

    public static Role defaultRole() {
        return Role.ROLE_USER;
    }

    /**
     * Converts a string to the corresponding {@link Role} enum constant.
     * <p>
     * This method supports both role names with and without the {@code ROLE_} prefix.
     * The comparison is case-insensitive.
     * <ul>
     *     <li>Input "ADMIN" or "ROLE_ADMIN" → Returns {@link Role#ROLE_ADMIN}</li>
     *     <li>Input "user" or "ROLE_USER" → Returns {@link Role#ROLE_USER}</li>
     *     <li>Invalid role names will result in an {@link IllegalArgumentException}.</li>
     * </ul>
     *
     * @param name the string representation of the role, with or without the {@code ROLE_} prefix
     * @return the corresponding {@link Role} enum constant
     * @throws IllegalArgumentException if the provided role name is invalid, null, or empty
     */
    public static Role fromName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }

        String formattedName = name.toUpperCase();
        if (!formattedName.startsWith("ROLE_")) {
            formattedName = "ROLE_" + formattedName;
        }

        try {
            return Role.valueOf(formattedName);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role name: '" + name + "'");
        }
    }

}
