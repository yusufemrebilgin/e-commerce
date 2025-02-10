package com.example.ecommerce.address.repository;

import com.example.ecommerce.address.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    long countAddressByUserId(String userId);

    boolean existsByTitleAndUserId(String title, String userId);

    Optional<Address> findByIdAndUserId(Long addressId, String userId);

    List<Address> findAllByUserId(String userId);

}
