package de.codecentric.spring_modulith_example.catalog.external_events;

import de.codecentric.spring_modulith_example.inventory.ProductCreated;

/**
 * The Catalog and Inventory modules communicate via events. Still, there exists a cyclic dependency on the conceptual
 * level. First, the Catalog module reacts to {@link de.codecentric.spring_modulith_example.inventory.QuantityChanged}
 * events to update the current stock of a product. Second, the Inventory module needs to know when a new product is
 * added to the catalog in order to set its initial stock.
 * <br/>
 * On the physical type level, we resolve this cyclic dependency by letting only the Catalog module depend on the
 * Inventory module. Technically, we invert the original dependency of the Inventory module on the Catalog module by
 * means of the {@link ProductCreated} interface: The Inventory module treats this interface as an event (see the
 * ProductCreatedListener class in the Inventory module) which can be implemented and published by other modules to
 * inform the Inventory module about new products with initial stock.
 * <br/>
 * This class is the Catalog module's implementation of {@link ProductCreated}.
 */
public class InventoryProductCreated implements ProductCreated {
    private Long productId;
    private int quantity;

    // Required for event (de)serialization
    public InventoryProductCreated() {
        // NOOP
    }

    public InventoryProductCreated(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    @Override
    public Long getProductId() {
        return productId;
    }

    // Required for event (de)serialization
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    // Required for event (de)serialization
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
