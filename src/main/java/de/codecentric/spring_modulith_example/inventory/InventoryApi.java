package de.codecentric.spring_modulith_example.inventory;

import de.codecentric.spring_modulith_example.inventory.repository.StockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * This controller exposes an HTTP API to application-external clients and in the modulithic application is only visible
 * to the Inventory module. By contrast to the Catalog module's HTTP API, the accessibility of this controller is
 * technically determined by Java's builtin package-scope visibility (which Spring Modulith also interprets to
 * encapsulate module-internal code).
 */
@RestController
class InventoryApi {
    private final InventoryData inventoryData;
    private final StockRepository stockRepository;

    InventoryApi(InventoryData inventoryData, StockRepository stockRepository) {
        this.inventoryData = inventoryData;
        this.stockRepository = stockRepository;
    }

    @PostMapping("inventory/stock/{productId}")
    void purchase(@PathVariable long productId, @RequestParam int purchaseQuantity) {
        var currentQuantity = getQuantity(productId);
        if (currentQuantity == 0 || (currentQuantity - purchaseQuantity) < 0)
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED);
        addStock(productId, purchaseQuantity * -1);
    }

    @PutMapping("inventory/stock/{productId}")
    void addStock(@PathVariable long productId, @RequestParam int quantity) {
        if (stockRepository.findByProductId(productId) == null
            || !inventoryData.addStockAndPublishQuantityChangedEvent(productId, quantity))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping("inventory/stock/{productId}")
    Integer getQuantity(@PathVariable long productId) {
        var stockEntry = stockRepository.findByProductId(productId);
        if (stockEntry == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return stockEntry.getQuantity();
    }
}
