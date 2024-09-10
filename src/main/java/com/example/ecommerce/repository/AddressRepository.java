package com.example.ecommerce.repository;

import com.example.ecommerce.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

    long countAddressByUserId(Long userId);

    boolean existsByTitleAndUserId(String title, Long userId);

}
