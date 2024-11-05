package com.example.ecommerce.mapper;

import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.embeddable.Area;
import com.example.ecommerce.model.embeddable.Location;
import com.example.ecommerce.payload.request.address.UpdateAddressRequest;
import com.example.ecommerce.payload.response.AddressResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper implements Mapper<Address, AddressResponse> {

    @Override
    public AddressResponse mapToResponse(@NonNull Address address) {
        return new AddressResponse(
                address.getId(),
                address.getTitle(),
                address.getArea(),
                address.getLocation()
        );
    }

    public void updateAddressFromRequest(@NonNull UpdateAddressRequest request, @NonNull Address existingAddress) {
        existingAddress.setTitle(request.title());

        Area area = existingAddress.getArea();
        if (area == null) {
            area = new Area();
            existingAddress.setArea(area);
        }
        area.setCity(request.city());
        area.setDistrict(request.district());
        area.setPostalCode(request.postalCode());

        Location location = existingAddress.getLocation();
        if (location == null) {
            location = new Location();
            existingAddress.setLocation(location);
        }
        location.setNeighbourhood(request.neighbourhood());
        location.setStreet(request.street());
        location.setBuilding(request.building());
        location.setAddressDetails(request.addressDetails());
    }

}
