package com.medipatient.doctor.dto;

import com.medipatient.doctor.model.Doctor;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDoctorDto {
    
    private UUID specialtyId;
    private String licenseNumber;
    
    @PositiveOrZero(message = "Consultation fee must be positive or zero")
    private Integer consultationFee;
    
    private Doctor.AvailabilityStatus availabilityStatus;
}