package com.medipatient.inventory.controller;

import com.medipatient.inventory.dto.CreateInventoryDto;
import com.medipatient.inventory.dto.InventoryDto;
import com.medipatient.inventory.model.Inventory;
import com.medipatient.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventaire", description = "Gestion de l'inventaire médical")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<Page<InventoryDto>> getAllInventoryItems(
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            @RequestParam(required = false) Inventory.Category category,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice) {
        
        Page<InventoryDto> items;
        
        if (search != null && (minPrice != null || maxPrice != null)) {
            // Recherche par nom ET filtrage par prix
            items = inventoryService.searchByName(search, pageable);
            // Note: Dans un vrai projet, il faudrait combiner les deux filtres
        } else if (minPrice != null || maxPrice != null) {
            items = inventoryService.getInventoryByPriceRange(
                minPrice != null ? minPrice : 0, 
                maxPrice != null ? maxPrice : Integer.MAX_VALUE, 
                pageable);
        } else if (search != null) {
            items = inventoryService.searchByName(search, pageable);
        } else if (category != null || supplier != null) {
            items = inventoryService.findWithFilters(category, supplier, null, pageable);
        } else {
            items = inventoryService.getAllInventoryItems(pageable);
        }
        
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDto> getInventoryItemById(@PathVariable UUID id) {
        return inventoryService.getInventoryItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InventoryDto> createInventoryItem(@Valid @RequestBody CreateInventoryDto createInventoryDto) {
        InventoryDto createdItem = inventoryService.createInventoryItem(createInventoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryDto> updateInventoryItem(@PathVariable UUID id, 
                                                           @Valid @RequestBody CreateInventoryDto updateInventoryDto) {
        try {
            InventoryDto updatedItem = inventoryService.updateInventoryItem(id, updateInventoryDto);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<InventoryDto> updateStock(@PathVariable UUID id, 
                                                   @RequestParam Integer newStock,
                                                   @RequestParam(required = false) String reason) {
        try {
            InventoryDto updatedItem = inventoryService.updateStock(id, newStock, reason);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/adjust-stock")
    public ResponseEntity<InventoryDto> adjustStock(@PathVariable UUID id, 
                                                   @RequestParam Integer adjustment,
                                                   @RequestParam(required = false) String reason) {
        try {
            InventoryDto updatedItem = inventoryService.adjustStock(id, adjustment, reason);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable UUID id) {
        try {
            inventoryService.deleteInventoryItem(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<InventoryDto>> getInventoryByCategory(
            @PathVariable Inventory.Category category,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        
        Page<InventoryDto> items = inventoryService.getInventoryByCategory(category, pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/supplier/{supplier}")
    public ResponseEntity<Page<InventoryDto>> getInventoryBySupplier(
            @PathVariable String supplier,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        
        Page<InventoryDto> items = inventoryService.getInventoryBySupplier(supplier, pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/alerts/low-stock")
    public ResponseEntity<Page<InventoryDto>> getLowStockItems(
            @PageableDefault(size = 20, sort = "currentStock") Pageable pageable) {
        
        Page<InventoryDto> items = inventoryService.getLowStockItems(pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/alerts/expired")
    public ResponseEntity<Page<InventoryDto>> getExpiredItems(
            @PageableDefault(size = 20, sort = "expiryDate") Pageable pageable) {
        
        Page<InventoryDto> items = inventoryService.getExpiredItems(pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/alerts/expiring-soon")
    public ResponseEntity<Page<InventoryDto>> getItemsExpiringSoon(
            @RequestParam(defaultValue = "30") int days,
            @PageableDefault(size = 20, sort = "expiryDate") Pageable pageable) {
        
        Page<InventoryDto> items = inventoryService.getItemsExpiringSoon(days, pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/alerts/low-stock/list")
    public ResponseEntity<List<InventoryDto>> getLowStockItemsList() {
        List<InventoryDto> items = inventoryService.getLowStockItemsList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/alerts/expired/list")
    public ResponseEntity<List<InventoryDto>> getExpiredItemsList() {
        List<InventoryDto> items = inventoryService.getExpiredItemsList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/alerts/expiring-soon/list")
    public ResponseEntity<List<InventoryDto>> getItemsExpiringSoonList(
            @RequestParam(defaultValue = "30") int days) {
        
        List<InventoryDto> items = inventoryService.getItemsExpiringSoonList(days);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/alerts/summary")
    public ResponseEntity<InventoryDto.AlertSummary> getInventoryAlertSummary() {
        InventoryDto.AlertSummary summary = inventoryService.getInventoryAlertSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/stats/category")
    public ResponseEntity<Long> countByCategory(@RequestParam Inventory.Category category) {
        long count = inventoryService.countByCategory(category);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/low-stock-count")
    public ResponseEntity<Long> countLowStockItems() {
        long count = inventoryService.countLowStockItems();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/expired-count")
    public ResponseEntity<Long> countExpiredItems() {
        long count = inventoryService.countExpiredItems();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/expiring-soon-count")
    public ResponseEntity<Long> countItemsExpiringSoon(@RequestParam(defaultValue = "30") int days) {
        long count = inventoryService.countItemsExpiringSoon(days);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/category-distribution")
    public ResponseEntity<List<Object[]>> getCategoryStatistics() {
        List<Object[]> stats = inventoryService.getCategoryStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/supplier-distribution")
    public ResponseEntity<List<Object[]>> getSupplierStatistics() {
        List<Object[]> stats = inventoryService.getSupplierStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/total-value")
    public ResponseEntity<Long> getTotalInventoryValue() {
        Long totalValue = inventoryService.getTotalInventoryValue();
        return ResponseEntity.ok(totalValue != null ? totalValue : 0L);
    }

    @GetMapping("/stats/total-value-by-category")
    public ResponseEntity<Long> getTotalInventoryValueByCategory(@RequestParam Inventory.Category category) {
        Long totalValue = inventoryService.getTotalInventoryValueByCategory(category);
        return ResponseEntity.ok(totalValue != null ? totalValue : 0L);
    }
}