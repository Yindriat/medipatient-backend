package com.medipatient.prescription.dto;

import com.medipatient.prescription.model.Prescription;
import jakarta.validation.Valid;
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
public class UpdatePrescriptionDto {
    
    private LocalDate prescriptionDate;
    
    @Valid
    private List<MedicationDto> medications;
    
    private String instructions;
    private Prescription.Status status;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicationDto {
        private String name;
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;
    }
}