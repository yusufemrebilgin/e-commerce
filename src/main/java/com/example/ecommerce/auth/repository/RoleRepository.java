package com.example.ecommerce.auth.repository;

import com.example.ecommerce.auth.model.Role;
import com.example.ecommerce.auth.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName name);

}
