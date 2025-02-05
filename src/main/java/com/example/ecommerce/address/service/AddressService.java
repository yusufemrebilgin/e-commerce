package com.example.ecommerce.address.service;

import com.example.ecommerce.address.exception.AddressLimitExceededException;
import com.example.ecommerce.address.exception.AddressNotFoundException;
import com.example.ecommerce.address.exception.DuplicateAddressTitleException;
import com.example.ecommerce.address.mapper.AddressMapper;
import com.example.ecommerce.address.model.Address;
import com.example.ecommerce.address.model.embeddable.Area;
import com.example.ecommerce.address.model.embeddable.Location;
import com.example.ecommerce.address.payload.request.CreateAddressRequest;
import com.example.ecommerce.address.payload.request.UpdateAddressRequest;
import com.example.ecommerce.address.payload.response.AddressResponse;
import com.example.ecommerce.address.repository.AddressRepository;
import com.example.ecommerce.auth.model.User;
import com.example.ecommerce.auth.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AuthService authService;
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;

    private static final int MAX_ADDRESSES = 5;

    /**
     * Retrieves an address by its ID.
     *
     * @param addressId ID of the address to retrieve
     * @return found {@link Address}
     * @throws AddressNotFoundException if address is not found
     */
    public Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId).orElseThrow(() -> new AddressNotFoundException(addressId));
    }

    /**
     * Retrieves all addresses for the currently authenticated user.
     *
     * @return a list of {@link AddressResponse} representing the user's addresses
     */
    public List<AddressResponse> getAllAddresses() {
        User currentUser = authService.getCurrentUser();
        List<Address> addresses = addressRepository.findAllByUserId(currentUser.getId());
        return addressMapper.mapToResponseList(addresses, addressMapper);
    }

    /**
     * Create a new address for the currently authenticated user.
     *
     * @param request a {@link CreateAddressRequest} containing the address details
     * @return newly created {@link AddressResponse}
     * @throws DuplicateAddressTitleException if user already has an address with same title
     * @throws AddressLimitExceededException  if user has reached the maximum number of addresses
     */
    public AddressResponse createAddress(CreateAddressRequest request) {

        User currentUser = authService.getCurrentUser();
        checkAddressTitleForUser(request.title(), currentUser);

        long addressCount = addressRepository.countAddressByUserId(currentUser.getId());

        if (addressCount >= MAX_ADDRESSES) {
            throw new AddressLimitExceededException();
        }

        Area area = new Area(
                request.city(),
                request.district(),
                request.postalCode()
        );

        Location location = new Location(
                request.neighbourhood(),
                request.street(),
                request.building(),
                request.addressDetails()
        );

        Address address = Address.builder()
                .title(request.title())
                .area(area)
                .location(location)
                .user(currentUser)
                .build();

        return addressMapper.mapToResponse(addressRepository.save(address));
    }

    /**
     * Updates an existing address.
     *
     * @param addressId ID of the address to update
     * @param request   a {@link UpdateAddressRequest} containing the updated details
     * @return updated {@link AddressResponse}
     * @throws AddressNotFoundException       if address with given ID is not found
     * @throws DuplicateAddressTitleException if user already has an address with same title
     */
    public AddressResponse updateAddress(Long addressId, UpdateAddressRequest request) {
        Address existingAddress = getAddressById(addressId);
        checkAddressTitleForUser(request.title(), existingAddress.getUser());
        addressMapper.updateAddressFromRequest(request, existingAddress);
        return addressMapper.mapToResponse(addressRepository.save(existingAddress));
    }

    /**
     * Delete an address by its ID.
     *
     * @param addressId ID of the address to delete
     * @throws AddressNotFoundException if address with given ID is not found
     */
    public void deleteAddress(Long addressId) {
        Address addressToBeDeleted = getAddressById(addressId);
        addressRepository.delete(addressToBeDeleted);
    }

    /**
     * Checks if the user already has an address with the given title.
     *
     * @param title the title of address to check
     * @param user  the user whose addresses to check
     * @throws DuplicateAddressTitleException if an address with same title already exists
     */
    private void checkAddressTitleForUser(String title, User user) {
        if (addressRepository.existsByTitleAndUserId(title, user.getId())) {
            throw new DuplicateAddressTitleException(title);
        }
    }

}
