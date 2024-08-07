package com.example.ecommerce.repository;

import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName name);

}
