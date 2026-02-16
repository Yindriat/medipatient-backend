package com.medipatient.stock.dto;

import com.medipatient.stock.model.StockMovement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStockMovementDto {
    
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    @NotNull(message = "Movement type is required")
    private StockMovement.MovementType movementType;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    private String reason;
    private UUID userId;
}