package com.example.ecommerce.address.factory;

import com.example.ecommerce.address.model.Address;
import com.example.ecommerce.address.model.embeddable.Area;
import com.example.ecommerce.address.model.embeddable.Location;
import com.example.ecommerce.address.payload.request.CreateAddressRequest;
import com.example.ecommerce.address.payload.request.UpdateAddressRequest;
import com.example.ecommerce.address.payload.response.AddressResponse;
import com.example.ecommerce.auth.model.User;

public final class AddressFactory {

    private AddressFactory() {}

    public static Address address() {
        return address("Default Title");
    }

    public static Address address(String title) {
        return Address.builder()
                .id(1L)
                .title(title)
                .area(defaultArea())
                .location(defaultLocation())
                .user(defaultUser())
                .build();
    }

    public static AddressResponse response(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getTitle(),
                address.getArea(),
                address.getLocation()
        );
    }

    public static CreateAddressRequest createRequest() {
        return new CreateAddressRequest(
                "Title",
                "Neighbourhood",
                "Street",
                "Building",
                "City",
                "District",
                "Postal Code",
                "Details"
        );
    }

    public static UpdateAddressRequest updateRequest() {
        return new UpdateAddressRequest(
                "Updated Title",
                "Updated Neighbourhood",
                "Updated Street",
                "Updated Building",
                "Updated City",
                "Updated District",
                "Updated Postal Code",
                "Updated Details"
        );
    }

    private static Area defaultArea() {
        return new Area(
                "Default City",
                "Default District",
                "Default Postal Code"
        );
    }

    private static Location defaultLocation() {
        return new Location(
                "Default Neighbourhood",
                "Default Street",
                "Default Building",
                "Default Address Details"
        );
    }

    private static User defaultUser() {
        return User.builder()
                .id("default-user-id")
                .name("Default User")
                .username("test-user")
                .password("test-pw")
                .email("test@example.com")
                .build();
    }

}
