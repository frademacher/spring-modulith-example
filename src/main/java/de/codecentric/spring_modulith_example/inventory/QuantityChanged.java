package de.codecentric.spring_modulith_example.inventory;

import org.springframework.context.ApplicationEvent;

/**
 * Event fired by the Inventory module when the quantity of some product's stock got changed (see
 * {@link InventoryData#addStockAndPublishQuantityChangedEvent(Long, int)}. Note that this event is a module-external
 * type because it resides in the top-level package of the Inventory module and has public visibility. Therefore, other
 * modules can directly refer to this event type without violating the intended modulithic structure of the application.
 */
public class QuantityChanged extends ApplicationEvent {
    private final Long productId;
    private final int newQuantity;

    public QuantityChanged(Object source, Long productId, int newQuantity) {
        super(source);
        this.productId = productId;
        this.newQuantity = newQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public int getNewQuantity() {
        return newQuantity;
    }
}
