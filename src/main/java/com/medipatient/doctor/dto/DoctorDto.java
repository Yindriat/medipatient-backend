package com.medipatient.doctor.dto;

import com.medipatient.doctor.model.Doctor;
import com.medipatient.profile.dto.ProfileDto;
import com.medipatient.specialty.dto.SpecialtyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    private UUID id;
    private ProfileDto user;
    private SpecialtyDto specialty;
    private String licenseNumber;
    private Integer consultationFee;
    private Doctor.AvailabilityStatus availabilityStatus;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}