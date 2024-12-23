package de.codecentric.spring_modulith_example.catalog.controller;

import de.codecentric.spring_modulith_example.catalog.model.Product;
import de.codecentric.spring_modulith_example.catalog.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This controller exposes an HTTP API to application-external clients. However, in the modulithic application this
 * controller will not be visible to other modules because it is contained in a sub-package of the Catalog module.
 */
@RestController
public class ProductApi {
    public static final int CATALOG_PAGE_SIZE = 20;

    private final ProductRepository productRepository;

    public ProductApi(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("catalog")
    public List<Long> getProducts(@RequestParam int pageNumber) {
        // Usually, we don't want to expose technical IDs (here: from the database) for several reasons. See
        // http://toddfredrich.com/ids-in-rest-api.html for a brief summary of the issue. However, to keep the codebase
        // concise and focused on its core concern (illustrating the usage various techniques of Spring Modulith), we
        // expose and process technical IDs in our HTTP APIs.
        return productRepository.findAll(PageRequest.of(pageNumber, CATALOG_PAGE_SIZE))
            .map(Product::getId)
            .toList();
    }

    @GetMapping("catalog/products/{productId}")
    public GetProductResponse getProduct(@PathVariable long productId) {
        var product = productRepository.findById(productId).orElseThrow();
        return new GetProductResponse(product.getName(), product.getDescription(),
            product.getPrice().getNumberStripped().toPlainString(), product.getPrice().getCurrency().getCurrencyCode());
    }

    public record GetProductResponse(String name, String description, String priceAmount, String priceCurrency) {
        // NOOP
    }

    @GetMapping("catalog/products/outOfStock")
    public List<Long> getOutOfStockProducts() {
        return productRepository.findByCurrentQuantityLessThan(1).stream().map(Product::getId).toList();
    }
}
