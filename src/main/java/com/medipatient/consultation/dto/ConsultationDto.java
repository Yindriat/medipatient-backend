package com.medipatient.consultation.dto;

import com.medipatient.appointment.dto.AppointmentDto;
import com.medipatient.doctor.dto.DoctorDto;
import com.medipatient.patient.dto.PatientDto;
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
public class ConsultationDto {
    private UUID id;
    private AppointmentDto appointment;
    private PatientDto patient;
    private DoctorDto doctor;
    private ZonedDateTime consultationDate;
    private String symptoms;
    private String diagnosis;
    private String treatmentPlan;
    private Map<String, Object> vitals;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}