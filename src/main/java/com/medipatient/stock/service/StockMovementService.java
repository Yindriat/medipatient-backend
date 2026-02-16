package com.medipatient.stock.service;

import com.medipatient.inventory.model.Inventory;
import com.medipatient.inventory.repository.InventoryRepository;
import com.medipatient.profile.model.Profile;
import com.medipatient.profile.repository.ProfileRepository;
import com.medipatient.stock.dto.CreateStockMovementDto;
import com.medipatient.stock.dto.StockMovementDto;
import com.medipatient.stock.mapper.StockMovementMapper;
import com.medipatient.stock.model.StockMovement;
import com.medipatient.stock.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final InventoryRepository inventoryRepository;
    private final ProfileRepository profileRepository;
    private final StockMovementMapper stockMovementMapper;

    @Transactional(readOnly = true)
    public Page<StockMovementDto> getAllStockMovements(Pageable pageable) {
        return stockMovementRepository.findAll(pageable)
                .map(stockMovementMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementDto> findWithFilters(UUID productId, StockMovement.MovementType movementType,
                                                 UUID userId, ZonedDateTime dateFrom, ZonedDateTime dateTo,
                                                 Pageable pageable) {
        return stockMovementRepository.findWithFilters(productId, movementType, userId, dateFrom, dateTo, pageable)
                .map(stockMovementMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementDto> getMovementsByProduct(UUID productId, Pageable pageable) {
        return stockMovementRepository.findByProductId(productId, pageable)
                .map(stockMovementMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementDto> getMovementsByUser(UUID userId, Pageable pageable) {
        return stockMovementRepository.findByUserId(userId, pageable)
                .map(stockMovementMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementDto> getMovementsByType(StockMovement.MovementType movementType, Pageable pageable) {
        return stockMovementRepository.findByMovementType(movementType, pageable)
                .map(stockMovementMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementDto> getMovementsByDateRange(ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable) {
        return stockMovementRepository.findByCreatedAtBetween(startDate, endDate, pageable)
                .map(stockMovementMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementDto> searchByReason(String reason, Pageable pageable) {
        return stockMovementRepository.findByReasonContainingIgnoreCase(reason, pageable)
                .map(stockMovementMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<StockMovementDto> getStockMovementById(UUID id) {
        return stockMovementRepository.findById(id)
                .map(stockMovementMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<StockMovementDto> getMovementHistoryForProduct(UUID productId) {
        return stockMovementRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(stockMovementMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StockMovementDto> getTodaysMovements() {
        return stockMovementRepository.findTodaysMovements()
                .stream()
                .map(stockMovementMapper::toDto)
                .toList();
    }

    public StockMovementDto createStockMovement(CreateStockMovementDto createStockMovementDto) {
        Inventory product = inventoryRepository.findById(createStockMovementDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + createStockMovementDto.getProductId()));

        // Vérifier que la quantité ne rend pas le stock négatif pour les sorties
        if (createStockMovementDto.getMovementType() == StockMovement.MovementType.SORTIE) {
            Integer currentStock = product.getCurrentStock();
            if (currentStock < createStockMovementDto.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock. Current: " + currentStock + 
                                                 ", requested: " + createStockMovementDto.getQuantity());
            }
        }

        StockMovement stockMovement = stockMovementMapper.toEntity(createStockMovementDto);
        stockMovement.setProduct(product);

        // Associer l'utilisateur si fourni
        if (createStockMovementDto.getUserId() != null) {
            Profile user = profileRepository.findById(createStockMovementDto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + createStockMovementDto.getUserId()));
            stockMovement.setUser(user);
        }

        // Mettre à jour le stock du produit
        Integer newStock;
        if (createStockMovementDto.getMovementType() == StockMovement.MovementType.ENTRÉE) {
            newStock = product.getCurrentStock() + createStockMovementDto.getQuantity();
        } else {
            newStock = product.getCurrentStock() - createStockMovementDto.getQuantity();
        }
        
        product.setCurrentStock(newStock);
        inventoryRepository.save(product);

        StockMovement savedMovement = stockMovementRepository.save(stockMovement);
        
        log.info("Created stock movement for product {} - Type: {}, Quantity: {}, New stock: {}", 
                product.getName(), createStockMovementDto.getMovementType(), 
                createStockMovementDto.getQuantity(), newStock);
        
        return stockMovementMapper.toDto(savedMovement);
    }

    public void deleteStockMovement(UUID id) {
        StockMovement stockMovement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stock movement not found with id: " + id));

        // Reverser le mouvement de stock
        Inventory product = stockMovement.getProduct();
        Integer reversalQuantity;
        
        if (stockMovement.getMovementType() == StockMovement.MovementType.ENTRÉE) {
            // Reverser une entrée = faire une sortie
            reversalQuantity = product.getCurrentStock() - stockMovement.getQuantity();
            if (reversalQuantity < 0) {
                throw new IllegalArgumentException("Cannot reverse movement - would result in negative stock");
            }
        } else {
            // Reverser une sortie = faire une entrée
            reversalQuantity = product.getCurrentStock() + stockMovement.getQuantity();
        }
        
        product.setCurrentStock(reversalQuantity);
        inventoryRepository.save(product);

        stockMovementRepository.delete(stockMovement);
        
        log.info("Deleted stock movement {} and reversed stock for product {} to {}", 
                id, product.getName(), reversalQuantity);
    }

    @Transactional(readOnly = true)
    public long countByMovementType(StockMovement.MovementType movementType) {
        return stockMovementRepository.countByMovementType(movementType);
    }

    @Transactional(readOnly = true)
    public long countByProductId(UUID productId) {
        return stockMovementRepository.countByProductId(productId);
    }

    @Transactional(readOnly = true)
    public long countByUserId(UUID userId) {
        return stockMovementRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getMovementTypeStatistics() {
        return stockMovementRepository.countByMovementTypeGrouped();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getMovementStatisticsByDate(ZonedDateTime startDate) {
        return stockMovementRepository.getMovementStatisticsByDate(startDate);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getNetMovementsByProduct(ZonedDateTime startDate) {
        return stockMovementRepository.getNetMovementsByProduct(startDate);
    }

    @Transactional(readOnly = true)
    public Long getTotalQuantityByProductAndTypeAfterDate(UUID productId, 
                                                         StockMovement.MovementType movementType,
                                                         ZonedDateTime startDate) {
        return stockMovementRepository.getTotalQuantityByProductAndTypeAfterDate(productId, movementType, startDate);
    }
}