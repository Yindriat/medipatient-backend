package com.medipatient.consultation.controller;

import com.medipatient.consultation.dto.ConsultationDto;
import com.medipatient.consultation.dto.CreateConsultationDto;
import com.medipatient.consultation.dto.UpdateConsultationDto;
import com.medipatient.consultation.service.ConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
@Tag(name = "Consultations", description = "Gestion des consultations médicales")
public class ConsultationController {

    private final ConsultationService consultationService;

    @GetMapping
    public ResponseEntity<Page<ConsultationDto>> getAllConsultations(
            @PageableDefault(size = 20, sort = "consultationDate") Pageable pageable,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTo,
            @RequestParam(required = false) String diagnosis,
            @RequestParam(required = false) String search) {
        
        Page<ConsultationDto> consultations;
        
        if (search != null) {
            consultations = consultationService.searchByKeyword(search, pageable);
        } else if (patientId != null || doctorId != null || dateFrom != null || dateTo != null || diagnosis != null) {
            consultations = consultationService.findWithFilters(patientId, doctorId, dateFrom, dateTo, diagnosis, pageable);
        } else {
            consultations = consultationService.getAllConsultations(pageable);
        }
        
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultationDto> getConsultationById(@PathVariable UUID id) {
        return consultationService.getConsultationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ConsultationDto> createConsultation(@Valid @RequestBody CreateConsultationDto createConsultationDto) {
        try {
            ConsultationDto createdConsultation = consultationService.createConsultation(createConsultationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdConsultation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultationDto> updateConsultation(@PathVariable UUID id, 
                                                             @Valid @RequestBody UpdateConsultationDto updateConsultationDto) {
        try {
            ConsultationDto updatedConsultation = consultationService.updateConsultation(id, updateConsultationDto);
            return ResponseEntity.ok(updatedConsultation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable UUID id) {
        try {
            consultationService.deleteConsultation(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<ConsultationDto>> getConsultationsByPatient(
            @PathVariable UUID patientId,
            @PageableDefault(size = 20, sort = "consultationDate") Pageable pageable) {
        
        Page<ConsultationDto> consultations = consultationService.getConsultationsByPatient(patientId, pageable);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<Page<ConsultationDto>> getConsultationsByDoctor(
            @PathVariable UUID doctorId,
            @PageableDefault(size = 20, sort = "consultationDate") Pageable pageable) {
        
        Page<ConsultationDto> consultations = consultationService.getConsultationsByDoctor(doctorId, pageable);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<ConsultationDto>> getConsultationsByAppointment(@PathVariable UUID appointmentId) {
        List<ConsultationDto> consultations = consultationService.getConsultationsByAppointment(appointmentId);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<ConsultationDto>> getConsultationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate,
            @PageableDefault(size = 20, sort = "consultationDate") Pageable pageable) {
        
        Page<ConsultationDto> consultations = consultationService.getConsultationsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/today")
    public ResponseEntity<List<ConsultationDto>> getTodaysConsultations() {
        List<ConsultationDto> consultations = consultationService.getTodaysConsultations();
        return ResponseEntity.ok(consultations);
    }

    @GetMapping("/stats/doctor/{doctorId}")
    public ResponseEntity<Long> countByDoctor(@PathVariable UUID doctorId) {
        long count = consultationService.countByDoctorId(doctorId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/patient/{patientId}")
    public ResponseEntity<Long> countByPatient(@PathVariable UUID patientId) {
        long count = consultationService.countByPatientId(patientId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/by-date")
    public ResponseEntity<List<Object[]>> getConsultationStatisticsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate) {
        
        List<Object[]> stats = consultationService.getConsultationStatisticsByDate(startDate);
        return ResponseEntity.ok(stats);
    }
}