package com.medipatient.patient.dto;

import com.medipatient.patient.model.Patient;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.medipatient.patient.model.Gender;


import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePatientDto {
    
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