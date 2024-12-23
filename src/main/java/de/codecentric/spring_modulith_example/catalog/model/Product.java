package de.codecentric.spring_modulith_example.catalog.model;

import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CompositeType;
import org.javamoney.moneta.Money;

@Entity
@Table
public class Product {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @AttributeOverride(
        name = "amount",
        column = @Column(name = "price_amount")
    )
    @AttributeOverride(
        name = "currency",
        column = @Column(name = "price_currency")
    )
    @CompositeType(MonetaryAmountType.class)
    @NotNull
    private Money price;

    private Integer currentQuantity;

    public Product() {
        // NOOP
    }

    public Product(String name, String description, Money price, Integer currentQuantity) {
        this(null, name, description, price, currentQuantity);
    }

    public Product(Long id, String name, String description, Money price, Integer currentQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.currentQuantity = currentQuantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }


    public Integer getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Integer currentQuantity) {
        this.currentQuantity = currentQuantity;
    }
}
