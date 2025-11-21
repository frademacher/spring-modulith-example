package de.codecentric.spring_modulith_example.catalog;

import de.codecentric.spring_modulith_example.catalog.external_events.InventoryProductCreated;
import de.codecentric.spring_modulith_example.catalog.model.CatalogData;
import de.codecentric.spring_modulith_example.catalog.model.Product;
import de.codecentric.spring_modulith_example.catalog.repository.ProductRepository;
import de.codecentric.spring_modulith_example.inventory.QuantityChanged;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import static de.codecentric.spring_modulith_example.shared.Defaults.DEFAULT_CURRENCY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration tests for the Catalog module that assume a running database (here: embedded H2). Note that we explicitly
 * include the Inventory module in the tests using the {@link ApplicationModuleTest#extraIncludes()} property. That is
 * because the test in {@link #testInitialProductEvents()} expects the occurrence of events that are handled and emitted
 * by the Inventory module.
 */
@ApplicationModuleTest(extraIncludes = "inventory")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CatalogIntegrationTests {
    private final EventPublicationRepository eventPublicationRepository;
    private final ProductRepository productRepository;

    CatalogIntegrationTests(EventPublicationRepository eventPublicationRepository,
        ProductRepository productRepository) {
        this.eventPublicationRepository = eventPublicationRepository;
        this.productRepository = productRepository;
    }

    /**
     * Test the handling and occurrence of events that are expected during the Spring application's initialization. More
     * precisely, this test ensures that for each initial product created by the Spring event listener
     * {@link CatalogData#initialData(ApplicationStartedEvent)} both an {@link InventoryProductCreated} and a
     * {@link QuantityChanged} event were fired. As a side effect, this test illustrates the usage of Spring Modulith's
     * {@link EventPublicationRepository} that permits access to Spring Modulith's database-level management of module
     * events.
     */
    @Test
    void testInitialProductEvents() {
        // In case the test gets executed immediately after application startup, give Spring Modulith some time to mark
        // all events from the application's initialization phase as completed. This check is equal to expecting that
        // no exceptions occurred during events' execution which would otherwise hinder event completion.
        await()
            .atMost(Duration.ofSeconds(1))
            .until(() -> eventPublicationRepository.findIncompletePublications().isEmpty());

        // Now that we know that Spring Modulith completed all events successfully, iterate over the events and for each
        // product keep track of InventoryProductCreated and QuantityChanged events
        var initialProductIdsWithInventoryProductCreatedEvent = new HashSet<Long>();
        var initialProducts = mapIdToQuantityForAllProducts();
        var initialProductIdsWithQuantityChangedEvent = new HashSet<Long>();
        for (var tep : eventPublicationRepository.findCompletedPublications()) {
            switch (tep.getEvent()) {
                case InventoryProductCreated e ->
                    assertThat(initialProductIdsWithInventoryProductCreatedEvent.add(e.getProductId())).isTrue();

                case QuantityChanged e -> {
                    if (initialProducts.containsKey(e.getProductId())) {
                        // Here, we also verify that the quantity of the product stored in the Catalog module's database
                        // is equal to the quantity comprised in the QuantityChanged event fired during the product's
                        // initial creation as a reaction of the Inventory module to the corresponding
                        // InventoryProductCreated event.
                        assertThat(initialProducts).containsEntry(e.getProductId(), e.getNewQuantity());

                        assertThat(initialProductIdsWithQuantityChangedEvent.add(e.getProductId())).isTrue();
                    }
                }

                default -> {
                    // NOOP
                }
            }
        }

        // Verify, that for each initial product an InventoryProductCreated and QuantityChanged event got fired
        assertThat(initialProductIdsWithInventoryProductCreatedEvent).containsAll(initialProducts.keySet());
        assertThat(initialProductIdsWithQuantityChangedEvent).containsAll(initialProducts.keySet());
    }

    private Map<Long, Integer> mapIdToQuantityForAllProducts() {
        return productRepository
            .findAll()
            .stream()
            .collect(Collectors.toMap(Product::getId, Product::getCurrentQuantity));
    }

    /**
     * Test that as per {@link CatalogData#quantityChanged(QuantityChanged)} the occurrence of a {@link QuantityChanged}
     * event results in an update of the current quantity of the corresponding {@link Product} in the Catalog module's
     * database. This test illustrates the usage of Spring Modulith's {@link Scenario} API, and more precisely of its
     * means for publishing events, waiting for state changes in Spring beans, and verifying the latter.
     */
    @Test
    void testQuantityChangedEvent(Scenario scenario) {
        // Create a test product in the Catalog module's database
        var initialQuantity = 100;
        var product = productRepository.save(new Product("Some Test Product", "", Money.of(100, DEFAULT_CURRENCY),
            initialQuantity));

        // Fire QuantityChanged event for new quantity of the test product and verify the event's impact
        var newQuantity = initialQuantity + 100;
        scenario
            .publish(new QuantityChanged(this, product.getId(), newQuantity))
            .andWaitForStateChange(
                () -> productRepository.findById(product.getId()),
                optProduct -> optProduct.isPresent() && optProduct.get().getCurrentQuantity() == newQuantity
            )
            .andVerify(optProduct -> {
                var productId = optProduct.orElseThrow().getId();
                var productWithNewQuantity = productRepository.findById(productId).orElseThrow();
                // Note that this assertion is equal to the previous andWaitForStateChange() method that allows test
                // continuation only if the test product's current quantity got effectively in the Catalog module's
                // database
                assertThat(newQuantity).isEqualTo(productWithNewQuantity.getCurrentQuantity());
            });

        // Clean up
        productRepository.delete(product);
    }
}