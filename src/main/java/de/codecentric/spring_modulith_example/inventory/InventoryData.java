package de.codecentric.spring_modulith_example.inventory;

import de.codecentric.spring_modulith_example.inventory.model.Stock;
import de.codecentric.spring_modulith_example.inventory.repository.StockRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class InventoryData {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final StockRepository stockRepository;

    InventoryData(ApplicationEventPublisher applicationEventPublisher, StockRepository stockRepository) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.stockRepository = stockRepository;
    }

    /**
     * Store new {@link Stock} in database and fire a {@link QuantityChanged} event in case of success.
     */
    @Transactional
    public boolean addStockAndPublishQuantityChangedEvent(Long productId, int quantity) {
        try {
            var stockEntry = stockRepository.findByProductId(productId);
            if (stockEntry == null)
                stockEntry = new Stock(productId, 0);
            stockEntry.setQuantity(stockEntry.getQuantity() + quantity);
            stockRepository.save(stockEntry);
            applicationEventPublisher.publishEvent(
                new QuantityChanged(this, stockEntry.getProductId(), stockEntry.getQuantity())
            );
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
