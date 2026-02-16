package com.medipatient.billing.dto;

import com.medipatient.appointment.dto.AppointmentDto;
import com.medipatient.billing.model.Invoice;
import com.medipatient.patient.dto.PatientDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private UUID id;
    private PatientDto patient;
    private AppointmentDto appointment;
    private String invoiceNumber;
    private Integer amount;
    private Invoice.Status status;
    private LocalDate dueDate;
    private List<InvoiceItemDto> items;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItemDto {
        private String description;
        private Integer quantity;
        private Integer unitPrice;
        private Integer totalPrice;
    }
}