package com.medipatient.inventory.dto;

import com.medipatient.inventory.model.Inventory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInventoryDto {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private Inventory.Category category;
    private String description;
    
    @PositiveOrZero(message = "Current stock must be positive or zero")
    private Integer currentStock;
    
    @PositiveOrZero(message = "Min stock must be positive or zero")
    private Integer minStock;
    
    @PositiveOrZero(message = "Unit price must be positive or zero")
    private Integer unitPrice;
    
    private LocalDate expiryDate;
    private String supplier;
}