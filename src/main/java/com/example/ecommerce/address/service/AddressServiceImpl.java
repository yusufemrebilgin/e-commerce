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
import com.example.ecommerce.auth.service.UserContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private static final int MAX_ADDRESSES = 5;

    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;

    private final UserContextService userContextService;

    @Override
    public List<AddressResponse> getAllAddressesForUser() {
        User currentUser = userContextService.getCurrentUser();
        List<Address> addresses = addressRepository.findAllByUserId(currentUser.getId());
        return addressMapper.mapToResponseList(addresses);
    }

    @Override
    public AddressResponse createAddress(CreateAddressRequest request) {

        User currentUser = userContextService.getCurrentUser();
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

    @Override
    public AddressResponse updateAddress(Long addressId, UpdateAddressRequest request) {
        Address existingAddress = getAddressById(addressId);
        checkAddressTitleForUser(request.title(), existingAddress.getUser());
        addressMapper.updateAddressFromRequest(request, existingAddress);
        return addressMapper.mapToResponse(addressRepository.save(existingAddress));
    }

    @Override
    public void deleteAddress(Long addressId) {
        Address addressToBeDeleted = getAddressById(addressId);
        addressRepository.delete(addressToBeDeleted);
    }

    private Address getAddressById(Long addressId) {
        User currentUser = userContextService.getCurrentUser();
        return addressRepository.findByIdAndUserId(addressId, currentUser.getId())
                .orElseThrow(() -> new AddressNotFoundException(addressId));
    }

    private void checkAddressTitleForUser(String title, User user) {
        if (addressRepository.existsByTitleAndUserId(title, user.getId())) {
            throw new DuplicateAddressTitleException(title);
        }
    }

}
