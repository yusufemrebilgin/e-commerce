package com.example.ecommerce.factory;

import com.example.ecommerce.model.User;

public final class UserFactory {

    private UserFactory() {
    }

    public static User user() {
        return User.builder()
                .id(1L)
                .name("Test User")
                .username("testUser")
                .password("test_user_password")
                .email("test@example.com")
                .build();
    }


}
