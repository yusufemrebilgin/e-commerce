package com.example.ecommerce.service;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.user.RoleNotFoundException;
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
    void givenValidSetOfRoleNames_whenRolesFound_assignRoles() {
        // given
        given(roleRepository.findByRoleName(ROLE_USER)).willReturn(Optional.of(userRole));
        given(roleRepository.findByRoleName(ROLE_ADMIN)).willReturn(Optional.of(adminRole));

        Set<Role> expected = Set.of(userRole, adminRole);

        // when & then
        Set<Role> actual = roleService.assignRoles(roleNames);

        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual).contains(userRole, adminRole);
        verify(roleRepository, times(2)).findByRoleName(any(RoleName.class));
    }

    @Test
    void givenValidSetOfRoleNames_whenRoleNotFound_throwRoleNotFoundException() {
        // given
        given(roleRepository.findByRoleName(ROLE_USER)).willReturn(Optional.of(userRole));
        given(roleRepository.findByRoleName(ROLE_ADMIN)).willReturn(Optional.empty());

        // when & then
        RoleNotFoundException ex = catchThrowableOfType(
                () -> roleService.assignRoles(roleNames),
                RoleNotFoundException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessage(ErrorMessages.ROLE_NOT_FOUND.message(ROLE_ADMIN));
        verify(roleRepository, times(1)).findByRoleName(ROLE_USER);
        verify(roleRepository, times(1)).findByRoleName(ROLE_ADMIN);
    }

    @Test
    void givenEmptySetOfRoleNames_whenDefaultUserRoleFound_assignDefaultRole() {
        // given
        given(roleRepository.findByRoleName(ROLE_USER)).willReturn(Optional.of(userRole));

        Set<Role> expected = Set.of(userRole);

        // when & then
        Set<Role> actual = roleService.assignDefaultRole();

        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual).contains(userRole);
        verify(roleRepository, times(1)).findByRoleName(any(RoleName.class));
    }

    @Test
    void givenEmptySetOfRoleNames_whenDefaultUserRoleNotFound_createUserRoleAndAssign() {
        // given
        given(roleRepository.save(any(Role.class))).willReturn(userRole);

        Set<Role> expected = Set.of(userRole);

        // when & then
        Set<Role> actual = roleService.assignDefaultRole();

        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual).contains(userRole);
        verify(roleRepository, times(1)).findByRoleName(any(RoleName.class));
    }

}
