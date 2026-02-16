package com.medipatient.prescription.dto;

import com.medipatient.prescription.model.Prescription;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class CreatePrescriptionDto {
    
    private UUID consultationId;
    
    @NotNull(message = "Patient ID is required")
    private UUID patientId;
    
    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;
    
    private LocalDate prescriptionDate;
    
    @NotEmpty(message = "At least one medication is required")
    @Valid
    private List<MedicationDto> medications;
    
    private String instructions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicationDto {
        @NotNull(message = "Medication name is required")
        private String name;
        
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;
    }
}