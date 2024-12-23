package de.codecentric.spring_modulith_example.catalog.model;

import de.codecentric.spring_modulith_example.catalog.external_events.InventoryProductCreated;
import de.codecentric.spring_modulith_example.catalog.repository.ProductRepository;
import de.codecentric.spring_modulith_example.inventory.QuantityChanged;
import org.javamoney.moneta.Money;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static de.codecentric.spring_modulith_example.shared.Defaults.DEFAULT_CURRENCY;

@Component
public class CatalogData {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ProductRepository productRepository;

    public CatalogData(ApplicationEventPublisher applicationEventPublisher, ProductRepository productRepository) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.productRepository = productRepository;
    }

    /**
     * Add some initial products for illustrative purposes. See how
     * {@code CatalogIntegrationTests.testInitialProductEvents()} checks for the occurrence of the events
     * {@link InventoryProductCreated} and {@link QuantityChanged} fired during initial product creation.
     */
    @EventListener(ApplicationStartedEvent.class)
    @Transactional
    public void initialData(ApplicationStartedEvent event) {
        createProductAndPublishProductCreatedEvent("Product 1", "Description of product 1", 100);
        createProductAndPublishProductCreatedEvent("Product 2", "Description of product 2", 200);
        createProductAndPublishProductCreatedEvent("Product 3", "Description of product 3", 300);
    }

    /**
     * Store new {@link Product} in database and afterwards fire an {@link InventoryProductCreated} event.
     */
    private void createProductAndPublishProductCreatedEvent(String name, String description,
        Number priceInDefaultCurrency) {
        var product = productRepository.save(
                new Product(name, description, Money.of(priceInDefaultCurrency, DEFAULT_CURRENCY), 0)
            );
        applicationEventPublisher.publishEvent(
            new InventoryProductCreated(product.getId(), product.getCurrentQuantity())
        );
    }

    /**
     * React to {@link QuantityChanged} events from the Inventory module and adapt the current quantity of the product
     * communicated by the event in the database.
     */
    @ApplicationModuleListener
    public void quantityChanged(QuantityChanged event) {
        var product = productRepository.findById(event.getProductId());
        if (product.isPresent()) {
            product.get().setCurrentQuantity(event.getNewQuantity());
            productRepository.save(product.get());
        } else
            throw new NoSuchElementException();
    }
}
