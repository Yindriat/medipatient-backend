package com.medipatient.appointment.controller;

import com.medipatient.appointment.dto.AppointmentDto;
import com.medipatient.appointment.dto.AvailableSlotDto;
import com.medipatient.appointment.dto.CreateAppointmentDto;
import com.medipatient.appointment.dto.UpdateAppointmentDto;
import com.medipatient.appointment.model.Appointment;
import com.medipatient.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Rendez-vous", description = "Gestion des rendez-vous médicaux")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<Page<AppointmentDto>> getAllAppointments(
            @PageableDefault(size = 20, sort = "appointmentDate,appointmentTime") Pageable pageable,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) Appointment.Status status,
            @RequestParam(required = false) Appointment.ConsultationType consultationType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        
        Page<AppointmentDto> appointments = appointmentService.findWithFilters(
                patientId, doctorId, status, consultationType, dateFrom, dateTo, pageable);
        
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable UUID id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AppointmentDto> createAppointment(@Valid @RequestBody CreateAppointmentDto createAppointmentDto) {
        try {
            AppointmentDto createdAppointment = appointmentService.createAppointment(createAppointmentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDto> updateAppointment(@PathVariable UUID id, 
                                                           @Valid @RequestBody UpdateAppointmentDto updateAppointmentDto) {
        try {
            AppointmentDto updatedAppointment = appointmentService.updateAppointment(id, updateAppointmentDto);
            return ResponseEntity.ok(updatedAppointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(@PathVariable UUID id, 
                                                                 @RequestParam Appointment.Status status) {
        try {
            AppointmentDto updatedAppointment = appointmentService.updateAppointmentStatus(id, status);
            return ResponseEntity.ok(updatedAppointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<AppointmentDto>> getAppointmentsByPatient(
            @PathVariable UUID patientId,
            @PageableDefault(size = 20, sort = "appointmentDate,appointmentTime") Pageable pageable) {
        
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsByPatient(patientId, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<Page<AppointmentDto>> getAppointmentsByDoctor(
            @PathVariable UUID doctorId,
            @PageableDefault(size = 20, sort = "appointmentDate,appointmentTime") Pageable pageable) {
        
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsByDoctor(doctorId, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<Page<AppointmentDto>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 20, sort = "appointmentTime") Pageable pageable) {
        
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsByDate(date, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<AppointmentDto>> getAppointmentsByStatus(
            @PathVariable Appointment.Status status,
            @PageableDefault(size = 20, sort = "appointmentDate,appointmentTime") Pageable pageable) {
        
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsByStatus(status, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/slots")
    public ResponseEntity<List<AvailableSlotDto>> getAvailableSlots(
            @RequestParam UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<AvailableSlotDto> slots = appointmentService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/today/confirmed")
    public ResponseEntity<List<AppointmentDto>> getTodaysConfirmedAppointments() {
        List<AppointmentDto> appointments = appointmentService.getTodaysConfirmedAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<AppointmentDto>> getOverdueAppointments() {
        List<AppointmentDto> appointments = appointmentService.getOverdueAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/stats/status")
    public ResponseEntity<Long> countByStatus(@RequestParam Appointment.Status status) {
        long count = appointmentService.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/consultation-type")
    public ResponseEntity<List<Object[]>> getConsultationTypeStatistics() {
        List<Object[]> stats = appointmentService.getConsultationTypeStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/payment-status")
    public ResponseEntity<List<Object[]>> getPaymentStatusStatistics() {
        List<Object[]> stats = appointmentService.getPaymentStatusStatistics();
        return ResponseEntity.ok(stats);
    }
}