package com.example.ecommerce.address.mapper;

import com.example.ecommerce.address.model.Address;
import com.example.ecommerce.address.model.embeddable.Area;
import com.example.ecommerce.address.model.embeddable.Location;
import com.example.ecommerce.address.payload.request.UpdateAddressRequest;
import com.example.ecommerce.address.payload.response.AddressResponse;
import com.example.ecommerce.shared.mapper.GenericMapper;
import lombok.NonNull;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper extends GenericMapper<Address, AddressResponse> {

    default void updateAddressFromRequest(@NonNull UpdateAddressRequest request, @NonNull Address address) {
        address.setTitle(request.title());

        Area area = address.getArea();
        if (area == null) {
            area = new Area();
            address.setArea(area);
        }
        area.setCity(request.city());
        area.setDistrict(request.district());
        area.setPostalCode(request.postalCode());

        Location location = address.getLocation();
        if (location == null) {
            location = new Location();
            address.setLocation(location);
        }
        location.setNeighbourhood(request.neighbourhood());
        location.setStreet(request.street());
        location.setBuilding(request.building());
        location.setAddressDetails(request.addressDetails());
    }

}
