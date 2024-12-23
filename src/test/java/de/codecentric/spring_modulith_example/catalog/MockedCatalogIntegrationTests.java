package de.codecentric.spring_modulith_example.catalog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.codecentric.spring_modulith_example.catalog.model.Product;
import de.codecentric.spring_modulith_example.catalog.repository.ProductRepository;
import de.codecentric.spring_modulith_example.inventory.QuantityChanged;
import de.codecentric.spring_modulith_example.inventory.model.Stock;
import de.codecentric.spring_modulith_example.inventory.repository.StockRepository;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static de.codecentric.spring_modulith_example.shared.Defaults.DEFAULT_CURRENCY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the Catalog module that mock the {@link ProductRepository}, thereby illustrating Spring
 * Modulith's interplay with Mockito. Note that we explicitly include the Inventory module in the tests using the
 * {@link ApplicationModuleTest#extraIncludes()} property. That is because the test in
 * {@link #testE2eQuantityChange(Scenario)} expects the occurrence of events that are handled and emitted by the
 * Inventory module.
 */
@ApplicationModuleTest(extraIncludes = "inventory")
@AutoConfigureMockMvc
class MockedCatalogIntegrationTests {
    @TestConfiguration
    public static class TestConfig {
        private static final AtomicLong ID = new AtomicLong(0);

        /**
         * Setup the {@link ProductRepository} mock
         */
        @Bean
        @Primary
        public ProductRepository mockProductRepository() {
            var productRepository = mock(ProductRepository.class);

            // Ensure that upon saving a new product to the repository (i) save() assigns a unique ID to the Product
            // instance; (ii) findById() returns the Product instance with the given ID; and (iii) save() returns an
            // Optional of the Product instance with the assigned unique ID
            when(productRepository.save(any(Product.class)))
                .thenAnswer(p -> {
                    var product = (Product) p.getArguments()[0];
                    product.setId(ID.incrementAndGet());
                    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
                    return product;
                });

            // Ensure that all paged requests to findAll() return the mock product. Note that this behavior is
            // inconsistent with the previous mock configuration, i.e., findAll() will not return any other product
            // save()d to the repository. Currently, this behavior is fine.
            when(productRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(MOCK_PRODUCT)));

            // Ensure that the mock product can be found with findById()
            when(productRepository.findById(MOCK_PRODUCT.getId())).thenReturn(Optional.of(MOCK_PRODUCT));

            return productRepository;
        }
    }

    private static final Product MOCK_PRODUCT = new Product(Long.MAX_VALUE, "Some Mock Product", "",
        Money.of(100, DEFAULT_CURRENCY), 1);

    private final MockMvc mockMvc;

    @MockitoBean
    private final StockRepository stockRepository;

    MockedCatalogIntegrationTests(MockMvc mockMvc, StockRepository stockRepository) {
        this.mockMvc = mockMvc;
        this.stockRepository = stockRepository;
    }

    /**
     * Perform end-to-end testing of product quantity changes in the sense that quantity changes triggered by
     * application-external clients using the application's HTTP API result in the expected module events to be fired
     * and handled so that the quantity change becomes externally visible from HTTP API calls. This test illustrates the
     * usage of Spring Modulith's {@link Scenario} API, and more precisely of its means for triggering stimuli (i.e.,
     * arbitrary calls to application logic) and wait for expected outcomes like certain module events to occur.
     */
    @Test
    void testE2eQuantityChange(Scenario scenario) {
        // Configure StockRepository mock to return a suitable Stock instance for the mock product
        when(stockRepository.findByProductId(MOCK_PRODUCT.getId()))
            .thenReturn(new Stock(MOCK_PRODUCT.getId(), MOCK_PRODUCT.getCurrentQuantity()));

        // Verify that product IDs can be queried from the Catalog module's HTTP API
        var productIds = httpGetAllProductIds();
        assertThat(productIds).isNotEmpty();

        // Retrieve the current quantity of a random product from the Inventory module's HTTP API
        var randomProductId = productIds.get(new Random().nextInt(productIds.size()));
        var currentQuantity = httpGetCurrentQuantity(randomProductId);

        // Verify that the change of a product's quantity by adding stock to it via the Inventory module's HTTP API
        // results in a QuantityChanged event that informs about the product's new (cumulated) quantity
        var addedQuantity = 100;
        var expectedNewQuantity = currentQuantity + addedQuantity;
        scenario
            .stimulate(() -> httpAddStock(randomProductId, addedQuantity))
            .andWaitForEventOfType(QuantityChanged.class)
            .toArriveAndVerify(event -> {
                assertThat(randomProductId).isEqualTo(event.getProductId());
                assertThat(expectedNewQuantity).isEqualTo(event.getNewQuantity());
            });

        // Verify that the new quantity is visible to external callers from the Inventory module's HTTP API
        currentQuantity = httpGetCurrentQuantity(randomProductId);
        assertThat(expectedNewQuantity).isEqualTo(currentQuantity);
    }

    private List<Long> httpGetAllProductIds() {
        return assertDoesNotThrow(() -> new ObjectMapper().readValue(
            mockMvc
                .perform(get("/catalog").param("pageNumber", "0"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>(){}
        ));
    }

    private Integer httpGetCurrentQuantity(Long productId) {
        return assertDoesNotThrow(() -> Integer.parseInt(
            mockMvc
                .perform(MockMvcRequestBuilders.get("/inventory/stock/%s".formatted(productId)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
        ));
    }

    private void httpAddStock(Long productId, int quantity) {
        assertDoesNotThrow(() -> mockMvc
            .perform(MockMvcRequestBuilders.put("/inventory/stock/%s".formatted(productId))
            .param("quantity", String.valueOf(quantity)))
            .andExpect(status().isOk()
        ));
    }
}
