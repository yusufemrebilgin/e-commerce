package com.example.ecommerce.service;

import com.example.ecommerce.exception.auth.RoleNotFoundException;
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

    /**
     * Assigns a set of roles based on given {@link RoleName} set.
     *
     * @param roleNames a set of {@link RoleName} to assign
     * @return a set of {@link Role} objects corresponding to given role names
     * @throws RoleNotFoundException if a role with the specified name is not found
     */
    public Set<Role> assignRoles(Set<RoleName> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (RoleName roleName : roleNames) {
            Role existingRole = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new RoleNotFoundException(roleName.name()));
            roles.add(existingRole);
        }

        return roles;
    }

    /**
     * Assign the default role {@code ROLE_USER} to a user if no other roles are specified.
     * If default role does not exist, it will be created.
     *
     * @return a set containing the default {@link Role} - {@code ROLE_USER}
     */
    public Set<Role> assignDefaultRole() {
        // If role is not found create user role as default
        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(0L, RoleName.ROLE_USER)));

        return Set.of(userRole);
    }

}
