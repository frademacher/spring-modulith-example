package de.codecentric.spring_modulith_example.inventory;

/**
 * The Catalog and Inventory modules communicate via events. Still, there exists a cyclic dependency on the conceptual
 * level. First, the Catalog module reacts to {@link de.codecentric.spring_modulith_example.inventory.QuantityChanged}
 * events to update the current stock of a product. Second, the Inventory module needs to know when a new product is
 * added to the catalog in order to set its initial stock.
 * <br/>
 * On the physical type level, we resolve this cyclic dependency by letting only the Catalog module depend on the
 * Inventory module. Technically, we invert the original dependency of the Inventory module on the Catalog module by
 * means of this interface: The Inventory module treats this interface as an event (see the
 * {@link ProductCreatedListener} class) which can be implemented and published by other modules to inform the Inventory
 * module about new products with initial stock (see the {@code InventoryProductCreated} class of the Catalog module).
 */
public interface ProductCreated {
    Long getProductId();
    Integer getQuantity();
}
