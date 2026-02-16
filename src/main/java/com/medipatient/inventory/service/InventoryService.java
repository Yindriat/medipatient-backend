package com.medipatient.inventory.service;

import com.medipatient.inventory.dto.CreateInventoryDto;
import com.medipatient.inventory.dto.InventoryDto;
import com.medipatient.inventory.mapper.InventoryMapper;
import com.medipatient.inventory.model.Inventory;
import com.medipatient.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Transactional(readOnly = true)
    public Page<InventoryDto> getAllInventoryItems(Pageable pageable) {
        return inventoryRepository.findAll(pageable)
                .map(inventoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InventoryDto> findWithFilters(Inventory.Category category, String supplier, 
                                             String search, Pageable pageable) {
        return inventoryRepository.findWithFilters(category, supplier, search, pageable)
                .map(inventoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InventoryDto> getInventoryByCategory(Inventory.Category category, Pageable pageable) {
        return inventoryRepository.findByCategory(category, pageable)
                .map(inventoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InventoryDto> searchByName(String name, Pageable pageable) {
        return inventoryRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(inventoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InventoryDto> getInventoryBySupplier(String supplier, Pageable pageable) {
        return inventoryRepository.findBySupplier(supplier, pageable)
                .map(inventoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InventoryDto> getInventoryByPriceRange(Integer minPrice, Integer maxPrice, Pageable pageable) {
        return inventoryRepository.findByUnitPriceBetween(minPrice, maxPrice, pageable)
                .map(inventoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<InventoryDto> getInventoryItemById(UUID id) {
        return inventoryRepository.findById(id)
                .map(inventoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InventoryDto> getLowStockItems(Pageable pageable) {
        return inventoryRepository.findLowStockItems(pageable)
                .map(inventoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InventoryDto> getExpiredItems(Pageable pageable) {
        return inventoryRepository.findExpiredItems(LocalDate.now(), pageable)
                .map(inventoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InventoryDto> getItemsExpiringSoon(int days, Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return inventoryRepository.findItemsExpiringSoon(today, futureDate, pageable)
                .map(inventoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<InventoryDto> getLowStockItemsList() {
        return inventoryRepository.findLowStockItems()
                .stream()
                .map(inventoryMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryDto> getExpiredItemsList() {
        return inventoryRepository.findExpiredItems(LocalDate.now())
                .stream()
                .map(inventoryMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryDto> getItemsExpiringSoonList(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return inventoryRepository.findItemsExpiringSoon(today, futureDate)
                .stream()
                .map(inventoryMapper::toDto)
                .toList();
    }

    public InventoryDto createInventoryItem(CreateInventoryDto createInventoryDto) {
        Inventory inventory = inventoryMapper.toEntity(createInventoryDto);
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        log.info("Created new inventory item with id: {} and name: {}", 
                savedInventory.getId(), savedInventory.getName());
        
        return inventoryMapper.toDto(savedInventory);
    }

    public InventoryDto updateInventoryItem(UUID id, CreateInventoryDto updateInventoryDto) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with id: " + id));

        inventoryMapper.updateEntityFromDto(updateInventoryDto, inventory);
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        log.info("Updated inventory item with id: {}", savedInventory.getId());
        
        return inventoryMapper.toDto(savedInventory);
    }

    public InventoryDto updateStock(UUID id, Integer newStock, String reason) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with id: " + id));

        Integer oldStock = inventory.getCurrentStock();
        inventory.setCurrentStock(newStock);
        
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        log.info("Updated stock for item {} from {} to {} - Reason: {}", 
                id, oldStock, newStock, reason != null ? reason : "Manual update");
        
        return inventoryMapper.toDto(savedInventory);
    }

    public InventoryDto adjustStock(UUID id, Integer adjustment, String reason) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with id: " + id));

        Integer oldStock = inventory.getCurrentStock();
        Integer newStock = oldStock + adjustment;
        
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative. Current stock: " + oldStock + ", adjustment: " + adjustment);
        }

        inventory.setCurrentStock(newStock);
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        log.info("Adjusted stock for item {} from {} to {} (adjustment: {}) - Reason: {}", 
                id, oldStock, newStock, adjustment, reason != null ? reason : "Manual adjustment");
        
        return inventoryMapper.toDto(savedInventory);
    }

    public void deleteInventoryItem(UUID id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with id: " + id));

        inventoryRepository.delete(inventory);
        log.info("Deleted inventory item with id: {}", id);
    }

    @Transactional(readOnly = true)
    public long countByCategory(Inventory.Category category) {
        return inventoryRepository.countByCategory(category);
    }

    @Transactional(readOnly = true)
    public long countLowStockItems() {
        return inventoryRepository.countLowStockItems();
    }

    @Transactional(readOnly = true)
    public long countExpiredItems() {
        return inventoryRepository.countExpiredItems(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public long countItemsExpiringSoon(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return inventoryRepository.countItemsExpiringSoon(today, futureDate);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getCategoryStatistics() {
        return inventoryRepository.countByCategory();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getSupplierStatistics() {
        return inventoryRepository.countBySupplier();
    }

    @Transactional(readOnly = true)
    public Long getTotalInventoryValue() {
        return inventoryRepository.getTotalInventoryValue();
    }

    @Transactional(readOnly = true)
    public Long getTotalInventoryValueByCategory(Inventory.Category category) {
        return inventoryRepository.getTotalInventoryValueByCategory(category);
    }

    @Transactional(readOnly = true)
    public InventoryDto.AlertSummary getInventoryAlertSummary() {
        return InventoryDto.AlertSummary.builder()
                .lowStockCount(countLowStockItems())
                .expiredCount(countExpiredItems())
                .expiringSoonCount(countItemsExpiringSoon(30))
                .totalValue(getTotalInventoryValue())
                .build();
    }

}