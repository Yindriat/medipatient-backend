package com.medipatient.billing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceDto {
    
    @NotNull(message = "Patient ID is required")
    private UUID patientId;
    
    private UUID appointmentId;
    
    private String invoiceNumber; // Auto-generated if not provided
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Integer amount;
    
    private LocalDate dueDate;
    
    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<InvoiceItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItemDto {
        @NotNull(message = "Description is required")
        private String description;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        @NotNull(message = "Unit price is required")
        @Positive(message = "Unit price must be positive")
        private Integer unitPrice;
        
        private Integer totalPrice; // Calculated automatically
    }
}