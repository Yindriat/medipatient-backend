package com.medipatient.billing.dto;

import com.medipatient.billing.model.Invoice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvoiceDto {
    
    @Positive(message = "Amount must be positive")
    private Integer amount;
    
    private Invoice.Status status;
    private LocalDate dueDate;
    
    @Valid
    private List<InvoiceItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItemDto {
        private String description;
        
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        @Positive(message = "Unit price must be positive")
        private Integer unitPrice;
        
        private Integer totalPrice;
    }
}