package com.example.ecommerce.address.controller;

import com.example.ecommerce.address.payload.request.CreateAddressRequest;
import com.example.ecommerce.address.payload.request.UpdateAddressRequest;
import com.example.ecommerce.address.payload.response.AddressResponse;
import com.example.ecommerce.address.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    /**
     * Retrieves all addresses for the authenticated user.
     *
     * @return a {@link ResponseEntity} containing a list of {@link AddressResponse}
     */
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddressesForUser() {
        return ResponseEntity.ok(addressService.getAllAddressesForUser());
    }

    /**
     * Creates a new address for the authenticated user.
     *
     * @param request the {@link CreateAddressRequest} containing address details
     * @return a {@link ResponseEntity} containing the created {@link AddressResponse}
     */
    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody CreateAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(request));
    }

    /**
     * Updates an existing address for the authenticated user.
     *
     * @param addressId ID of the address to update
     * @param request   the {@link UpdateAddressRequest} containing updated address details
     * @return a {@link ResponseEntity} containing the updated {@link AddressResponse}
     */
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody UpdateAddressRequest request
    ) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, request));
    }

    /**
     * Deletes an address for the authenticated user.
     *
     * @param addressId ID of the address to delete
     * @return a {@link ResponseEntity} with no content
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

}
