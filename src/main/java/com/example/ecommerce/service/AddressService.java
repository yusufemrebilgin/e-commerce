package com.example.ecommerce.service;

import com.example.ecommerce.exception.address.AddressLimitExceededException;
import com.example.ecommerce.exception.address.AddressNotFoundException;
import com.example.ecommerce.exception.address.DuplicateAddressTitleException;
import com.example.ecommerce.mapper.AddressMapper;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.User;
import com.example.ecommerce.payload.dto.AddressDto;
import com.example.ecommerce.payload.request.address.CreateAddressRequest;
import com.example.ecommerce.payload.request.address.UpdateAddressRequest;
import com.example.ecommerce.repository.AddressRepository;
import com.example.ecommerce.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AuthUtils authUtils;
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;

    private static final int MAX_ADDRESSES = 5;

    protected Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId).orElseThrow(() -> new AddressNotFoundException(addressId));
    }

    public List<AddressDto> getAllAddresses() {
        return addressMapper.mapToDtoList(addressRepository.findAll(), addressMapper);
    }

    public AddressDto createAddress(CreateAddressRequest request) {

        User user = authUtils.getCurrentUser();
        checkAddressTitle(request.title(), user);

        long addressCount = addressRepository.countAddressByUserId(user.getId());

        if (addressCount >= MAX_ADDRESSES) {
            throw new AddressLimitExceededException();
        }

        Address address = Address.builder()
                .title(request.title())
                .neighbourhood(request.neighbourhood())
                .street(request.street())
                .building(request.building())
                .city(request.city())
                .district(request.district())
                .postalCode(request.postalCode())
                .addressDetails(request.addressDetails())
                .user(user)
                .build();

        return addressMapper.mapToDto(addressRepository.save(address));
    }

    public AddressDto updateAddress(Long addressId, UpdateAddressRequest request) {
        Address address = getAddressById(addressId);
        checkAddressTitle(request.title(), address.getUser());
        addressMapper.updateAddressFromDto(request, address);
        return addressMapper.mapToDto(addressRepository.save(address));
    }

    public void deleteAddress(Long addressId) {
        Address address = getAddressById(addressId);
        addressRepository.delete(address);
    }

    private void checkAddressTitle(String title, User currentUser) {
        if (addressRepository.existsByTitleAndUserId(title, currentUser.getId())) {
            throw new DuplicateAddressTitleException(title);
        }
    }

}
