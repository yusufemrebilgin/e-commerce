package com.example.ecommerce.service;

import com.example.ecommerce.constant.ErrorMessages;
import com.example.ecommerce.exception.address.AddressLimitExceededException;
import com.example.ecommerce.exception.address.AddressNotFoundException;
import com.example.ecommerce.exception.address.DuplicateAddressTitleException;
import com.example.ecommerce.factory.AddressFactory;
import com.example.ecommerce.factory.UserFactory;
import com.example.ecommerce.mapper.AddressMapper;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.User;
import com.example.ecommerce.payload.request.address.CreateAddressRequest;
import com.example.ecommerce.payload.request.address.UpdateAddressRequest;
import com.example.ecommerce.payload.response.AddressResponse;
import com.example.ecommerce.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    AddressService addressService;

    @Mock
    AuthService authService;

    @Mock
    AddressMapper addressMapper;

    @Mock
    AddressRepository addressRepository;

    User testUser;

    @BeforeEach
    void setUp() {
        testUser = UserFactory.user();
    }

    @Test
    void givenValidAddressId_whenAddressFound_thenReturnAddress() {
        // given
        Address expected = AddressFactory.address();
        given(addressRepository.findById(anyLong())).willReturn(Optional.of(expected));

        // when
        Address actual = addressService.getAddressById(expected.getId());

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(addressRepository, times(1)).findById(expected.getId());
    }

    @Test
    void givenInvalidAddressId_whenAddressNotFound_thenThrowAddressNotFoundException() {
        // given
        Long addressId = 1L;
        given(addressRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        AddressNotFoundException ex = catchThrowableOfType(
                () -> addressService.getAddressById(addressId),
                AddressNotFoundException.class
        );

        // then
        then(ex).isNotNull();
        then(ex.getMessage()).isEqualTo(ErrorMessages.ADDRESS_NOT_FOUND.message(addressId));
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void givenValidCreateAddressRequest_whenAddressCreated_thenReturnAddressResponse() {
        // given
        given(authService.getCurrentUser()).willReturn(testUser);

        CreateAddressRequest createRequest = AddressFactory.createRequest();
        Address address = AddressFactory.address();
        AddressResponse expected = AddressFactory.response(address);
        given(addressRepository.save(any(Address.class))).willReturn(address);
        given(addressMapper.mapToResponse(any(Address.class))).willReturn(expected);

        // when
        AddressResponse actual = addressService.createAddress(createRequest);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void givenCreateAddressRequest_whenTitleExists_thenThrowDuplicateAddressTitleException() {
        // given
        CreateAddressRequest createRequest = AddressFactory.createRequest();
        given(authService.getCurrentUser()).willReturn(testUser);
        given(addressRepository.existsByTitleAndUserId(anyString(), anyLong())).willReturn(true);

        // when
        DuplicateAddressTitleException ex = catchThrowableOfType(
                () -> addressService.createAddress(createRequest),
                DuplicateAddressTitleException.class
        );

        // then
        then(ex).isNotNull();
        then(ex).hasMessageContaining(createRequest.title());
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void givenCreateAddressRequest_whenAddressCountReachesLimit_thenThrowAddressLimitExceededException() {
        // given
        CreateAddressRequest createRequest = AddressFactory.createRequest();
        given(authService.getCurrentUser()).willReturn(testUser);
        given(addressRepository.countAddressByUserId(anyLong())).willReturn(50L);

        // when
        AddressLimitExceededException ex = catchThrowableOfType(
                () -> addressService.createAddress(createRequest),
                AddressLimitExceededException.class
        );

        // then
        then(ex).isNotNull();
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void givenValidUpdateRequest_whenAddressUpdated_thenReturnUpdatedAddressResponse() {
        // given
        Address address = AddressFactory.address();
        given(addressRepository.findById(anyLong())).willReturn(Optional.of(address));

        UpdateAddressRequest updateRequest = AddressFactory.updateRequest();
        address.setTitle(updateRequest.title());
        given(addressRepository.save(any(Address.class))).willReturn(address);

        AddressResponse expected = AddressFactory.response(address);
        given(addressMapper.mapToResponse(any(Address.class))).willReturn(expected);

        // when
        AddressResponse actual = addressService.updateAddress(address.getId(), updateRequest);

        // then
        then(actual).isNotNull();
        then(actual.title()).isEqualTo(updateRequest.title());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void givenValidAddressId_whenAddressExists_thenDeleteExistingAddress() {
        // given
        Address address = AddressFactory.address();
        given(addressRepository.findById(address.getId())).willReturn(Optional.of(address));

        // when & then
        addressService.deleteAddress(address.getId());
        verify(addressRepository, times(1)).findById(address.getId());
        verify(addressRepository).delete(argThat(d -> d.getId().equals(address.getId())));
    }

    @Test
    void givenInvalidAddressId_whenAddressNotExists_thenThrowAddressNotFoundException() {
        // given
        Long addressId = 1L;
        given(addressRepository.findById(addressId)).willReturn(Optional.empty());

        // when & then
        AddressNotFoundException ex = catchThrowableOfType(
                () -> addressService.deleteAddress(addressId),
                AddressNotFoundException.class
        );

        then(ex).isNotNull();
        then(ex.getMessage()).isEqualTo(ErrorMessages.ADDRESS_NOT_FOUND.message(addressId));
        verify(addressRepository, never()).delete(any(Address.class));
    }

}
