package com.example.ecommerce.address.service;

import com.example.ecommerce.address.exception.AddressLimitExceededException;
import com.example.ecommerce.address.exception.AddressNotFoundException;
import com.example.ecommerce.address.exception.DuplicateAddressTitleException;
import com.example.ecommerce.address.factory.AddressFactory;
import com.example.ecommerce.address.mapper.AddressMapper;
import com.example.ecommerce.address.model.Address;
import com.example.ecommerce.address.payload.request.CreateAddressRequest;
import com.example.ecommerce.address.payload.request.UpdateAddressRequest;
import com.example.ecommerce.address.payload.response.AddressResponse;
import com.example.ecommerce.address.repository.AddressRepository;
import com.example.ecommerce.auth.model.User;
import com.example.ecommerce.auth.service.UserContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @InjectMocks
    AddressServiceImpl addressService;

    @Mock
    AddressMapper addressMapper;

    @Mock
    AddressRepository addressRepository;

    @Mock
    UserContextService userContextService;

    User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID().toString());
    }

    @Test
    void givenCurrentUser_whenGetAllAddressForUser_thenReturnAddressResponseList() {
        // given
        List<Address> addresses = List.of(
                AddressFactory.address("address-1"),
                AddressFactory.address("address-2"),
                AddressFactory.address("address-3")
        );

        List<AddressResponse> expected = addresses.stream()
                .map(AddressFactory::response)
                .toList();

        User user = addresses.get(0).getUser();

        given(userContextService.getCurrentUser()).willReturn(user);
        given(addressRepository.findAllByUserId(user.getId())).willReturn(addresses);
        given(addressMapper.mapToResponseList(addresses)).willReturn(expected);

        // when
        List<AddressResponse> actual = addressService.getAllAddressesForUser();

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual).hasSize(expected.size());
        verify(addressRepository, times(1)).findAllByUserId(user.getId());
    }

    @Test
    void givenValidCreateAddressRequest_whenAddressCreated_thenReturnAddressResponse() {
        // given
        CreateAddressRequest createRequest = AddressFactory.createRequest();
        Address address = AddressFactory.address(createRequest.title());
        AddressResponse expected = AddressFactory.response(address);

        given(userContextService.getCurrentUser()).willReturn(address.getUser());
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

        given(userContextService.getCurrentUser()).willReturn(testUser);
        given(addressRepository.existsByTitleAndUserId(anyString(), anyString())).willReturn(true);

        // when
        DuplicateAddressTitleException ex = catchThrowableOfType(
                DuplicateAddressTitleException.class,
                () -> addressService.createAddress(createRequest)
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
        given(userContextService.getCurrentUser()).willReturn(testUser);
        given(addressRepository.countAddressByUserId(anyString())).willReturn(50L);

        // when
        AddressLimitExceededException ex = catchThrowableOfType(
                AddressLimitExceededException.class,
                () -> addressService.createAddress(createRequest)
        );

        // then
        then(ex).isNotNull();
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void givenValidUpdateRequest_whenAddressUpdated_thenReturnUpdatedAddressResponse() {
        // given
        Address address = AddressFactory.address();
        given(userContextService.getCurrentUser()).willReturn(testUser);
        given(addressRepository.findByIdAndUserId(anyLong(), anyString())).willReturn(Optional.of(address));

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
        given(userContextService.getCurrentUser()).willReturn(testUser);
        given(addressRepository.findByIdAndUserId(address.getId(), testUser.getId())).willReturn(Optional.of(address));

        // when & then
        addressService.deleteAddress(address.getId());
        verify(addressRepository, times(1)).findByIdAndUserId(address.getId(), testUser.getId());
        verify(addressRepository).delete(argThat(d -> d.getId().equals(address.getId())));
    }

    @Test
    void givenInvalidAddressId_whenAddressNotExists_thenThrowAddressNotFoundException() {
        // given
        Long addressId = 1L;
        given(userContextService.getCurrentUser()).willReturn(testUser);
        given(addressRepository.findByIdAndUserId(addressId, testUser.getId())).willReturn(Optional.empty());

        // when & then
        AddressNotFoundException ex = catchThrowableOfType(
                AddressNotFoundException.class,
                () -> addressService.deleteAddress(addressId)
        );

        then(ex).isNotNull();
        then(ex).hasMessageContaining(addressId.toString());
        verify(addressRepository, never()).delete(any(Address.class));
    }

}
