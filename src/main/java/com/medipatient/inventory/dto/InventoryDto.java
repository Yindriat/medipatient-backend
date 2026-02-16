package com.medipatient.inventory.dto;

import com.medipatient.inventory.model.Inventory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {
    private UUID id;
    private String name;
    private Inventory.Category category;
    private String description;
    private Integer currentStock;
    private Integer minStock;
    private Integer unitPrice;
    private LocalDate expiryDate;
    private String supplier;
    private boolean isLowStock;
    private boolean isExpired;
    private boolean isExpiringSoon;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertSummary {
        private long lowStockCount;
        private long expiredCount;
        private long expiringSoonCount;
        private Long totalValue;
    }
}