package com.example.ecommerce.model;

import com.example.ecommerce.model.embeddable.Area;
import com.example.ecommerce.model.embeddable.Location;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Title of the address (e.g., "Home", "Office")
    private String title;

    @Embedded
    private Area area;

    @Embedded
    private Location location;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
