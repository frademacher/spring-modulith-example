package de.codecentric.spring_modulith_example.inventory;

import de.codecentric.spring_modulith_example.catalog.external_events.InventoryProductCreated;
import de.codecentric.spring_modulith_example.inventory.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.PublishedEvents;
import org.springframework.modulith.test.Scenario;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Inventory module that assume a running database (here: embedded H2).
 */
@ApplicationModuleTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureMockMvc
class InventoryIntegrationTests {
    private final StockRepository stockRepository;

    InventoryIntegrationTests(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Test that as per {@link ProductCreatedListener#productCreated(ProductCreated)} the receipt of an
     * {@link InventoryProductCreated} event results in the Inventory module to create a corresponding
     * {@link de.codecentric.spring_modulith_example.inventory.model.Stock} instance and store it in the database by
     * means of the {@link StockRepository}. This test illustrates the usage of Spring Modulith's {@link Scenario} API,
     * and more precisely of its means for publishing events and waiting for state changes in Spring beans. Furthermore,
     * the test shows how Spring Modulith's {@link PublishedEvents} abstraction can be used to check for the occurrence
     * of events in the course of a tested scenario. Note how {@code testInitialProductEvents()} in
     * {@link de.codecentric.spring_modulith_example.catalog.model.CatalogData} also checks for occurred events but
     * instead relies on the more technical {@link EventPublicationRepository} for this purpose because the checked
     * events occurred prior to and independent of any scenario-triggered events.
     */
    @Test
    void testInventoryProductCreatedHandling(Scenario scenario, PublishedEvents publishedEvents) {
        var quantity = 500;
        scenario
            .publish(new InventoryProductCreated(1L, quantity))
            .andWaitForStateChange(() -> stockRepository.findByProductId(1L));

        // While the above scenario only checks for the state change of the StockRepository, i.e., that an entry got
        // created for the new product, the following assertions verify that (i) exactly one QuantityChanged event
        // got fired as a reaction to the InventoryProductCreated event; and (ii) that this event informs about the
        // created product's quantity (i.e., the product's quantity in the database is equal to the product's "new
        // quantity" as transmitted in the QuantityChanged event).
        assertThat(publishedEvents.ofType(QuantityChanged.class)).hasSize(1);
        assertThat(
            publishedEvents.ofType(QuantityChanged.class).matchingValue(QuantityChanged::getNewQuantity, quantity)
        ).hasSize(1);
    }
}
