package com.example.ecommerce.service;

import com.example.ecommerce.constant.ErrorMessages;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    AddressService addressService;

    @Mock
    AuthUtils authUtils;

    @Mock
    AddressMapper addressMapper;

    @Mock
    AddressRepository addressRepository;

    User user;
    Address address;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        address = new Address();
        address.setId(1L);
        address.setTitle("Home");
        address.setUser(user);
    }

    @Test
    void givenAddressId_whenAddressFound_returnAddress() {
        // given
        given(addressRepository.findById(anyLong())).willReturn(Optional.of(address));

        // when & then
        Address actual = addressService.getAddressById(address.getId());

        then(actual).isNotNull();
        then(actual).isEqualTo(address);
        verify(addressRepository, times(1)).findById(anyLong());
    }

    @Test
    void givenAddressId_whenAddressNotFound_throwAddressNotFoundException() {
        // given
        given(addressRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        AddressNotFoundException ex = catchThrowableOfType(
                () -> addressService.getAddressById(1L),
                AddressNotFoundException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessage(ErrorMessages.ADDRESS_NOT_FOUND.message(1L));
        verify(addressRepository, times(1)).findById(anyLong());
    }

    @Test
    void givenCurrentUserId_whenAddressesFound_returnListOfAddressDto() {
        // given
        List<AddressDto> expected = List.of(new AddressDto());
        given(authUtils.getCurrentUser()).willReturn(user);
        given(addressRepository.findAllByUserId(anyLong())).willReturn(List.of(new Address()));
        given(addressMapper.mapToDtoList(anyList(), any(AddressMapper.class))).willReturn(expected);

        // when
        List<AddressDto> actual = addressService.getAllAddresses();

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        then(actual).hasSize(expected.size());
        verify(authUtils, times(1)).getCurrentUser();
        verify(addressRepository, times(1)).findAllByUserId(anyLong());
        verify(addressMapper, times(1)).mapToDtoList(anyList(), any(AddressMapper.class));
    }

    @Test
    void givenValidCreateAddressRequest_whenAddressCreated_returnAddressDto() {
        // given
        AddressDto expected = new AddressDto();
        CreateAddressRequest request = CreateAddressRequest.builder().build();

        given(authUtils.getCurrentUser()).willReturn(user);
        given(addressRepository.save(any(Address.class))).willReturn(address);
        given(addressMapper.mapToDto(any(Address.class))).willReturn(expected);

        // when
        AddressDto actual = addressService.createAddress(request);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(addressMapper, times(1)).mapToDto(any(Address.class));
    }

    @Test
    void givenCreateAddressRequestWithDuplicateTitle_whenTitleExists_throwDuplicateAddressTitleException() {
        // given
        given(authUtils.getCurrentUser()).willReturn(user);
        given(addressRepository.existsByTitleAndUserId(anyString(), anyLong())).willReturn(true);

        CreateAddressRequest request = CreateAddressRequest.builder().title(address.getTitle()).build();

        // when & then
        DuplicateAddressTitleException ex = catchThrowableOfType(
                () -> addressService.createAddress(request),
                DuplicateAddressTitleException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessage(ErrorMessages.DUPLICATE_ADDRESS_TITLE.message(address.getTitle()));
        verify(addressRepository, times(1)).existsByTitleAndUserId(anyString(), anyLong());
    }

    @Test
    void givenCreateAddressRequest_whenAddressCountReachesLimit_throwAddressLimitExceededException() {
        // given
        given(authUtils.getCurrentUser()).willReturn(user);
        given(addressRepository.countAddressByUserId(anyLong())).willReturn(1_000L);

        CreateAddressRequest request = CreateAddressRequest.builder().build();

        // when & then
        AddressLimitExceededException ex = catchThrowableOfType(
                () -> addressService.createAddress(request),
                AddressLimitExceededException.class
        );

        then(ex).isNotNull();
        then(ex).hasMessage(ErrorMessages.ADDRESS_LIMIT_EXCEEDED.message());
        verify(addressRepository, times(1)).countAddressByUserId(anyLong());
    }

    @Test
    void givenUpdateAddressRequest_whenAddressUpdated_returnUpdatedAddressDto() {
        // given
        AddressDto expected = new AddressDto();
        UpdateAddressRequest updateAddressRequest = UpdateAddressRequest.builder().build();

        given(addressRepository.findById(anyLong())).willReturn(Optional.of(address));
        given(addressRepository.save(address)).willReturn(address);
        given(addressMapper.mapToDto(address)).willReturn(expected);

        // when
        AddressDto actual = addressService.updateAddress(address.getId(), updateAddressRequest);

        // then
        then(actual).isNotNull();
        then(actual).isEqualTo(expected);
        verify(addressRepository, times(1)).findById(anyLong());
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(addressMapper, times(1)).mapToDto(any(Address.class));
    }

    @Test
    void givenAddressId_whenAddressFound_deleteExistingAddress() {
        // given
        given(addressRepository.findById(anyLong())).willReturn(Optional.of(address));

        // when & then
        addressService.deleteAddress(address.getId());

        verify(addressRepository, times(1)).findById(address.getId());
        verify(addressRepository, times(1)).delete(address);
    }

}
