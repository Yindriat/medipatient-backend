package com.medipatient.consultation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConsultationDto {
    
    private UUID appointmentId;
    
    @NotNull(message = "Patient ID is required")
    private UUID patientId;
    
    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;
    
    private ZonedDateTime consultationDate;
    private String symptoms;
    private String diagnosis;
    private String treatmentPlan;
    private Map<String, Object> vitals;
}