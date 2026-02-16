package com.medipatient.prescription.dto;

import com.medipatient.consultation.dto.ConsultationDto;
import com.medipatient.doctor.dto.DoctorDto;
import com.medipatient.patient.dto.PatientDto;
import com.medipatient.prescription.model.Prescription;
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
public class PrescriptionDto {
    private UUID id;
    private ConsultationDto consultation;
    private PatientDto patient;
    private DoctorDto doctor;
    private LocalDate prescriptionDate;
    private List<MedicationDto> medications;
    private String instructions;
    private Prescription.Status status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

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