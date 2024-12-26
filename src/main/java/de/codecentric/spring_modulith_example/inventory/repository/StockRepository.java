package de.codecentric.spring_modulith_example.inventory.repository;

import de.codecentric.spring_modulith_example.inventory.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByProductId(Long productId);
    List<Stock> findByQuantityLessThan(int quantity);
}
