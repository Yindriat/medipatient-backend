package com.medipatient.stock.dto;

import com.medipatient.inventory.dto.InventoryDto;
import com.medipatient.profile.dto.ProfileDto;
import com.medipatient.stock.model.StockMovement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDto {
    private UUID id;
    private InventoryDto product;
    private StockMovement.MovementType movementType;
    private Integer quantity;
    private String reason;
    private ProfileDto user;
    private ZonedDateTime createdAt;
}