package de.codecentric.spring_modulith_example.catalog.repository;

import de.codecentric.spring_modulith_example.catalog.model.Product;
import org.javamoney.moneta.Money;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> searchByNameOrDescriptionLikeIgnoreCase(String name, String description, PageRequest pageRequest);
    List<Product> findByPriceLessThanEqual(Money price, PageRequest pageRequest);
}
