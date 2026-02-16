package com.medipatient.patient.dto;

import com.medipatient.patient.model.Patient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.medipatient.patient.model.Patient;
import com.medipatient.patient.model.Gender;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientDto {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    private Gender gender;
    private String bloodType;
    private List<String> allergies;
    private List<String> chronicConditions;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
}