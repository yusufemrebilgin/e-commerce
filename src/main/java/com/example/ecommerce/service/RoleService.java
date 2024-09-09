package com.example.ecommerce.service;

import com.example.ecommerce.exception.user.RoleNotFoundException;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.enums.RoleName;
import com.example.ecommerce.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Set<Role> assignRoles(Set<RoleName> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (RoleName roleName : roleNames) {
            Role existingRole = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new RoleNotFoundException(roleName.name()));
            roles.add(existingRole);
        }

        return roles;
    }

    public Set<Role> assignDefaultRole() {
        // If role is not found create user role as default
        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(0L, RoleName.ROLE_USER)));

        return Set.of(userRole);
    }

}
