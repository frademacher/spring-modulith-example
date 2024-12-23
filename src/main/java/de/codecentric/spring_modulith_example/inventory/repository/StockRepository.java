package de.codecentric.spring_modulith_example.inventory.repository;

import de.codecentric.spring_modulith_example.inventory.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByProductId(Long productId);
}
