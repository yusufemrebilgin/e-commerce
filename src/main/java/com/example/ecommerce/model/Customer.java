package com.example.ecommerce.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("Customer")
public class Customer extends User {

    @OneToOne(mappedBy = "customer")
    private Cart cart;

    @OneToMany(
            mappedBy = "customer",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private List<Address> addresses = new ArrayList<>();

}
