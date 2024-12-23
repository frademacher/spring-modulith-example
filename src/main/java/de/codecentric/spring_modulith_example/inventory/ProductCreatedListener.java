package de.codecentric.spring_modulith_example.inventory;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
class ProductCreatedListener {
    private final InventoryData inventoryData;

    ProductCreatedListener(InventoryData inventoryData) {
        this.inventoryData = inventoryData;
    }

    /**
     * React to events based on the {@link ProductCreated} interface and fired by other modules to inform the Inventory
     * modules about newly created products
     */
    @ApplicationModuleListener
    void productCreated(ProductCreated event) {
        if (!inventoryData.addStockAndPublishQuantityChangedEvent(event.getProductId(), event.getQuantity()))
            throw new IllegalStateException("Creation of initial stock for product %s with quantity %s failed"
                .formatted(event.getProductId(), event.getQuantity()));
    }
}
