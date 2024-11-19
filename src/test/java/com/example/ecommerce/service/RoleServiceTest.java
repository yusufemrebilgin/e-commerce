package com.example.ecommerce.service;

import com.example.ecommerce.exception.auth.RoleNotFoundException;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.enums.RoleName;
import com.example.ecommerce.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static com.example.ecommerce.model.enums.RoleName.ROLE_ADMIN;
import static com.example.ecommerce.model.enums.RoleName.ROLE_USER;
import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    RoleService roleService;

    @Mock
    RoleRepository roleRepository;

    Role userRole;
    Role adminRole;

    Set<RoleName> roleNames;

    @BeforeEach
    void setUp() {
        userRole = new Role(1L, ROLE_USER);
        adminRole = new Role(2L, ROLE_ADMIN);
        roleNames = Set.of(ROLE_USER, ROLE_ADMIN);
    }

    @Test
    void givenValidSetOfRoleNames_whenAssignRoles_thenReturnSetOfRoles() {
        // given
        given(roleRepository.findByRoleName(ROLE_USER)).willReturn(Optional.of(userRole));
        given(roleRepository.findByRoleName(ROLE_ADMIN)).willReturn(Optional.of(adminRole));

        // when
        Set<Role> roles = roleService.assignRoles(roleNames);

        // then
        then(roles).isNotNull();
        then(roles).contains(userRole, adminRole);
        verify(roleRepository, times(2)).findByRoleName(any(RoleName.class));
    }

    @Test
    void givenValidSetOfRoleNames_whenGivenRoleNotFound_thenThrowRoleNotFoundException() {
        // given
        given(roleRepository.findByRoleName(ROLE_USER)).willReturn(Optional.empty());

        // when & then
        RoleNotFoundException ex = catchThrowableOfType(
                () -> roleService.assignRoles(Set.of(ROLE_USER)),
                RoleNotFoundException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(ROLE_USER.name());
        verify(roleRepository, times(1)).findByRoleName(any(RoleName.class));
    }

    @Test
    void givenEmptySetOfRoleNames_whenDefaultRoleFound_thenAssignDefaultRole() {
        // given
        given(roleRepository.findByRoleName(ROLE_USER)).willReturn(Optional.of(userRole));

        // when
        Set<Role> roles = roleService.assignDefaultRole();

        // then
        then(roles).isNotNull();
        then(roles).hasSize(1);
        then(roles).contains(userRole);
        verify(roleRepository, times(1)).findByRoleName(any(RoleName.class));
    }

    @Test
    void givenEmptySetOfRoleNames_whenDefaultRoleNotFound_thenCreateAndAssignDefaultRole() {
        // given
        given(roleRepository.findByRoleName(ROLE_USER)).willReturn(Optional.empty());
        given(roleRepository.save(any(Role.class))).willReturn(userRole);

        // when
        Set<Role> roles = roleService.assignDefaultRole();

        // then
        then(roles).isNotNull();
        then(roles).hasSize(1);
        then(roles).contains(userRole);
        verify(roleRepository, times(1)).findByRoleName(any(RoleName.class));
        verify(roleRepository, times(1)).save(any(Role.class));
    }

}
