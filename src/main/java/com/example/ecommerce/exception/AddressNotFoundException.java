package com.example.ecommerce.exception;

public class AddressNotFoundException extends NotFoundException {

    public AddressNotFoundException() {
        super(String.format(DEFAULT_FORMAT, "Address"));
    }

    public AddressNotFoundException(Long id) {
        super(String.format(DEFAULT_FORMAT_WITH_ID, "Address", id));
    }

    public AddressNotFoundException(String message) {
        super(message);
    }

}
