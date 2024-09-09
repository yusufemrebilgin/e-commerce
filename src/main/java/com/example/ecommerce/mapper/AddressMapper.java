package com.example.ecommerce.mapper;

import com.example.ecommerce.model.Address;
import com.example.ecommerce.payload.dto.AddressDto;
import com.example.ecommerce.payload.request.address.UpdateAddressRequest;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper implements Mapper<Address, AddressDto> {

    @Override
    public AddressDto mapToDto(@NonNull Address address) {
        return new AddressDto(
                address.getTitle(),
                new AddressDto.Area(
                        address.getCity(),
                        address.getDistrict(),
                        address.getPostalCode()
                ),
                new AddressDto.Location(
                        address.getNeighbourhood(),
                        address.getStreet(),
                        address.getBuilding(),
                        address.getAddressDetails()
                )
        );
    }

    public void updateAddressFromDto(@NonNull UpdateAddressRequest request, @NonNull Address existingAddress) {
        existingAddress.setTitle(request.title());
        existingAddress.setNeighbourhood(request.neighbourhood());
        existingAddress.setStreet(request.street());
        existingAddress.setBuilding(request.building());
        existingAddress.setCity(request.city());
        existingAddress.setDistrict(request.district());
        existingAddress.setPostalCode(request.postalCode());
        existingAddress.setAddressDetails(request.addressDetails());
    }

}
