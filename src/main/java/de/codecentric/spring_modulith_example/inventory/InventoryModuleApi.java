package de.codecentric.spring_modulith_example.inventory;

import de.codecentric.spring_modulith_example.inventory.model.Stock;
import de.codecentric.spring_modulith_example.inventory.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryModuleApi {
    private final StockRepository stockRepository;

    public InventoryModuleApi(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Long> getOutOfStockProducts() {
        return stockRepository.findByQuantityLessThan(1).stream().map(Stock::getProductId).toList();
    }
}
