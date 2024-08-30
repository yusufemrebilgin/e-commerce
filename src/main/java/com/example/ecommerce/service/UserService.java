package com.example.ecommerce.service;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Seller;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.enums.RoleName;
import com.example.ecommerce.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthUtils authUtils;

    protected User getCurrentUser() {
        return authUtils.getCurrentUser();
    }

    protected String getCurrentUsername() {
        return authUtils.getCurrentUsername();
    }

    protected Seller getCurrentSeller() {
        return getUserByRoleName(RoleName.ROLE_SELLER);
    }

    protected Customer getCurrentCustomer() {
        return getUserByRoleName(RoleName.ROLE_CUSTOMER);
    }

    @SuppressWarnings("unchecked")
    private <T extends User> T getUserByRoleName(RoleName roleName) {
        T user = (T) getCurrentUser();
        if (user.getRoles().stream().noneMatch(r -> r.getRoleName().equals(roleName))) {
            throw new IllegalArgumentException("User does not have the required role: " + roleName.name());
        }
        return user;
    }

}
