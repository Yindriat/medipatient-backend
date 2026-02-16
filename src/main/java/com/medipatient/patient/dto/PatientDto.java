package com.medipatient.patient.dto;

import com.medipatient.patient.model.Patient;
import com.medipatient.profile.dto.ProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import com.medipatient.patient.model.Gender;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {
    private UUID id;
    private ProfileDto user;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String bloodType;
    private List<String> allergies;
    private List<String> chronicConditions;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}