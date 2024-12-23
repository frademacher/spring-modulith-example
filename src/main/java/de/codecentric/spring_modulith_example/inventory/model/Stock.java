package de.codecentric.spring_modulith_example.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table
public class Stock {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique=true)
    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

    public Stock() {
        // NOOP
    }

    public Stock(Long productId, Integer quantity) {
        this(null, productId, quantity);
    }

    public Stock(Long id, Long productId, Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
