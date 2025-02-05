package com.example.ecommerce.address.repository;

import com.example.ecommerce.address.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    long countAddressByUserId(Long userId);

    boolean existsByTitleAndUserId(String title, Long userId);

    List<Address> findAllByUserId(Long userId);

}
