package com.example.ecommerce.product.model.embeddable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

    private Double percentage;
    private LocalDateTime start;
    private LocalDateTime end;

    @JsonIgnore
    public boolean isValid() {
        // All discount fields must be either all defined or all null
        boolean allDefined = percentage != null
                && start != null
                && end != null;

        boolean allNull = percentage == null
                && start == null
                && end == null;

        return allDefined || allNull;
    }

}
