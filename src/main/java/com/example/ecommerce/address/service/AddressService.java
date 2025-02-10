package com.example.ecommerce.address.service;

import com.example.ecommerce.address.exception.AddressLimitExceededException;
import com.example.ecommerce.address.exception.AddressNotFoundException;
import com.example.ecommerce.address.exception.DuplicateAddressTitleException;
import com.example.ecommerce.address.payload.request.CreateAddressRequest;
import com.example.ecommerce.address.payload.request.UpdateAddressRequest;
import com.example.ecommerce.address.payload.response.AddressResponse;

import java.util.List;

/**
 * Service interface for managing addresses in the system.
 * <p>
 * This service handles operations related to user addresses such as retrieving,
 * creating, updating, and deleting addresses. It ensures that addresses are properly validated
 * (e.g., duplicate titles and address limits) and that only authenticated users can manage their addresses.
 */
public interface AddressService {

    /**
     * Retrieves all addresses for the currently authenticated user.
     *
     * @return a list of {@link AddressResponse} containing the details of the user's addresses
     */
    List<AddressResponse> getAllAddressesForUser();

    /**
     * Creates a new address for the currently authenticated user.
     *
     * @param addressRequest the {@link CreateAddressRequest} containing the address details to be created
     * @return an {@link AddressResponse} containing the details of the newly created address
     * @throws DuplicateAddressTitleException if the address title already exists for the user
     * @throws AddressLimitExceededException  if the user exceeds the maximum allowed number of addresses
     */
    AddressResponse createAddress(CreateAddressRequest addressRequest);

    /**
     * Updates an existing address for the currently authenticated user.
     *
     * @param addressId      the ID of the address to be updated
     * @param addressRequest the {@link UpdateAddressRequest} containing the updated address details
     * @return an {@link AddressResponse} containing the details of the updated address
     * @throws AddressNotFoundException       if the address with the given ID does not exist or does not belong to the user
     * @throws DuplicateAddressTitleException if the updated address title already exists for the user
     */
    AddressResponse updateAddress(Long addressId, UpdateAddressRequest addressRequest);

    /**
     * Deletes an existing address for the currently authenticated user.
     *
     * @param addressId the ID of the address to be deleted
     * @throws AddressNotFoundException if the address with the given ID does not exist or does not belong to the user
     */
    void deleteAddress(Long addressId);

}
