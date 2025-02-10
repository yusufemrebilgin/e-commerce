package com.example.ecommerce.product.model;

import com.example.ecommerce.category.model.Category;
import com.example.ecommerce.product.model.embeddable.Discount;
import com.example.ecommerce.product.model.listener.ProductListener;
import com.example.ecommerce.shared.audit.Auditable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.Builder.Default;

@Entity
@Builder
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(ProductListener.class)
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;
    private String description;

    private int stock;
    private BigDecimal price;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "percentage", column = @Column(name = "discount_percentage")),
            @AttributeOverride(name = "start", column = @Column(name = "discount_start")),
            @AttributeOverride(name = "end", column = @Column(name = "discount_end"))
    })
    private Discount discount;

    @Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();

    public boolean isDiscountValid() {
        return discount != null && discount.isValid();
    }

    public boolean isDiscountActive() {
        if (!isDiscountValid()) {
            resetDiscount();
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(discount.getStart()) && now.isBefore(discount.getEnd());
    }

    public boolean isDiscountExpired() {
        if (!isDiscountValid()) {
            resetDiscount();
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(discount.getStart()) && now.isAfter(discount.getEnd());
    }

    public BigDecimal getDiscountedPrice() {
        if (isDiscountActive()) {
            return price.subtract(price.multiply(BigDecimal.valueOf(discount.getPercentage() / 100)));
        }
        return price; // if there is no discount return regular price
    }

    public boolean hasSufficientStock(int quantity) {
        return stock >= quantity;
    }

    public void resetDiscount() {
        this.setDiscount(null);
    }

}
