package de.codecentric.spring_modulith_example.catalog.nested_modules.search_optimization.controller;

import de.codecentric.spring_modulith_example.catalog.model.Product;
import de.codecentric.spring_modulith_example.catalog.repository.ProductRepository;
import org.javamoney.moneta.Money;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

import static de.codecentric.spring_modulith_example.catalog.controller.ProductApi.CATALOG_PAGE_SIZE;
import static de.codecentric.spring_modulith_example.shared.Defaults.DEFAULT_CURRENCY;

@RestController
public class SearchOptimizationApi {
    private final ProductRepository productRepository;

    public SearchOptimizationApi(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("catalog/search/by/nameAndDescription")
    public List<Long> searchProductsByNameAndDescription(@RequestParam String name, @RequestParam String description,
        @RequestParam int pageNumber) {
        return productRepository
            .searchByNameOrDescriptionLikeIgnoreCase(
                "%" + name + "%",
                "%" + description + "%",
                PageRequest.of(pageNumber, CATALOG_PAGE_SIZE)
            )
            .stream().map(Product::getId)
            .toList();
    }

    @GetMapping("catalog/search/by/maxPrice")
    public List<Long> searchProductsByMaxPrice(@RequestParam String price, @RequestParam int pageNumber) {
        var maxPrice = Money.of(new BigDecimal(price), DEFAULT_CURRENCY);
        return productRepository
            .findByPriceLessThanEqual(maxPrice, PageRequest.of(pageNumber, CATALOG_PAGE_SIZE))
            .stream().map(Product::getId)
            .toList();
    }
}
