package com.medipatient.appointment.dto;

import com.medipatient.appointment.model.Appointment;
import com.medipatient.doctor.dto.DoctorDto;
import com.medipatient.patient.dto.PatientDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private UUID id;
    private PatientDto patient;
    private DoctorDto doctor;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Appointment.ConsultationType consultationType;
    private Appointment.Status status;
    private String reason;
    private String notes;
    private Appointment.PaymentMethod paymentMethod;
    private Appointment.PaymentStatus paymentStatus;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}