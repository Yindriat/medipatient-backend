package com.medipatient.stock.controller;

import com.medipatient.stock.dto.CreateStockMovementDto;
import com.medipatient.stock.dto.StockMovementDto;
import com.medipatient.stock.model.StockMovement;
import com.medipatient.stock.service.StockMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
@Tag(name = "Mouvements de Stock", description = "Gestion des mouvements de stock")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @GetMapping
    public ResponseEntity<Page<StockMovementDto>> getAllStockMovements(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) StockMovement.MovementType movementType,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTo,
            @RequestParam(required = false) String reason) {
        
        Page<StockMovementDto> movements;
        
        if (reason != null) {
            movements = stockMovementService.searchByReason(reason, pageable);
        } else if (productId != null || movementType != null || userId != null || dateFrom != null || dateTo != null) {
            movements = stockMovementService.findWithFilters(productId, movementType, userId, dateFrom, dateTo, pageable);
        } else {
            movements = stockMovementService.getAllStockMovements(pageable);
        }
        
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovementDto> getStockMovementById(@PathVariable UUID id) {
        return stockMovementService.getStockMovementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StockMovementDto> createStockMovement(@Valid @RequestBody CreateStockMovementDto createStockMovementDto) {
        try {
            StockMovementDto createdMovement = stockMovementService.createStockMovement(createStockMovementDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMovement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockMovement(@PathVariable UUID id) {
        try {
            stockMovementService.deleteStockMovement(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<StockMovementDto>> getMovementsByProduct(
            @PathVariable UUID productId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<StockMovementDto> movements = stockMovementService.getMovementsByProduct(productId, pageable);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/product/{productId}/history")
    public ResponseEntity<List<StockMovementDto>> getMovementHistoryForProduct(@PathVariable UUID productId) {
        List<StockMovementDto> movements = stockMovementService.getMovementHistoryForProduct(productId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<StockMovementDto>> getMovementsByUser(
            @PathVariable UUID userId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<StockMovementDto> movements = stockMovementService.getMovementsByUser(userId, pageable);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/type/{movementType}")
    public ResponseEntity<Page<StockMovementDto>> getMovementsByType(
            @PathVariable StockMovement.MovementType movementType,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<StockMovementDto> movements = stockMovementService.getMovementsByType(movementType, pageable);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<StockMovementDto>> getMovementsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<StockMovementDto> movements = stockMovementService.getMovementsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/today")
    public ResponseEntity<List<StockMovementDto>> getTodaysMovements() {
        List<StockMovementDto> movements = stockMovementService.getTodaysMovements();
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/stats/type")
    public ResponseEntity<Long> countByMovementType(@RequestParam StockMovement.MovementType movementType) {
        long count = stockMovementService.countByMovementType(movementType);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/product/{productId}")
    public ResponseEntity<Long> countByProduct(@PathVariable UUID productId) {
        long count = stockMovementService.countByProductId(productId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<Long> countByUser(@PathVariable UUID userId) {
        long count = stockMovementService.countByUserId(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/type-distribution")
    public ResponseEntity<List<Object[]>> getMovementTypeStatistics() {
        List<Object[]> stats = stockMovementService.getMovementTypeStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/by-date")
    public ResponseEntity<List<Object[]>> getMovementStatisticsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate) {
        
        List<Object[]> stats = stockMovementService.getMovementStatisticsByDate(startDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/net-movements")
    public ResponseEntity<List<Object[]>> getNetMovementsByProduct(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate) {
        
        List<Object[]> stats = stockMovementService.getNetMovementsByProduct(startDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/total-quantity")
    public ResponseEntity<Long> getTotalQuantityByProductAndType(
            @RequestParam UUID productId,
            @RequestParam StockMovement.MovementType movementType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate) {
        
        Long total = stockMovementService.getTotalQuantityByProductAndTypeAfterDate(productId, movementType, startDate);
        return ResponseEntity.ok(total != null ? total : 0L);
    }
}