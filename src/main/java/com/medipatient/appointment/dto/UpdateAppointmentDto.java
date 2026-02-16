package com.medipatient.appointment.dto;

import com.medipatient.appointment.model.Appointment;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentDto {
    
    @Future(message = "Appointment date must be in the future")
    private LocalDate appointmentDate;
    
    private LocalTime appointmentTime;
    private Appointment.ConsultationType consultationType;
    private Appointment.Status status;
    private String reason;
    private String notes;
    private Appointment.PaymentMethod paymentMethod;
    private Appointment.PaymentStatus paymentStatus;
}