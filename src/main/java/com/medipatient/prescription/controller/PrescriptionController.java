package com.medipatient.prescription.controller;

import com.medipatient.prescription.dto.CreatePrescriptionDto;
import com.medipatient.prescription.dto.PrescriptionDto;
import com.medipatient.prescription.dto.UpdatePrescriptionDto;
import com.medipatient.prescription.model.Prescription;
import com.medipatient.prescription.service.PrescriptionService;
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
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Prescriptions", description = "Gestion des prescriptions médicales")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping
    public ResponseEntity<Page<PrescriptionDto>> getAllPrescriptions(
            @PageableDefault(size = 20, sort = "prescriptionDate") Pageable pageable,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) Prescription.Status status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String medicationName) {
        
        Page<PrescriptionDto> prescriptions;
        
        if (medicationName != null) {
            prescriptions = prescriptionService.findByMedicationName(medicationName, pageable);
        } else if (patientId != null || doctorId != null || status != null || dateFrom != null || dateTo != null) {
            prescriptions = prescriptionService.findWithFilters(patientId, doctorId, status, dateFrom, dateTo, pageable);
        } else {
            prescriptions = prescriptionService.getAllPrescriptions(pageable);
        }
        
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable UUID id) {
        return prescriptionService.getPrescriptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PrescriptionDto> createPrescription(@Valid @RequestBody CreatePrescriptionDto createPrescriptionDto) {
        try {
            PrescriptionDto createdPrescription = prescriptionService.createPrescription(createPrescriptionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPrescription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionDto> updatePrescription(@PathVariable UUID id, 
                                                             @Valid @RequestBody UpdatePrescriptionDto updatePrescriptionDto) {
        try {
            PrescriptionDto updatedPrescription = prescriptionService.updatePrescription(id, updatePrescriptionDto);
            return ResponseEntity.ok(updatedPrescription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PrescriptionDto> updatePrescriptionStatus(@PathVariable UUID id, 
                                                                   @RequestParam Prescription.Status status) {
        try {
            PrescriptionDto updatedPrescription = prescriptionService.updatePrescriptionStatus(id, status);
            return ResponseEntity.ok(updatedPrescription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable UUID id) {
        try {
            prescriptionService.deletePrescription(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<PrescriptionDto>> getPrescriptionsByPatient(
            @PathVariable UUID patientId,
            @PageableDefault(size = 20, sort = "prescriptionDate") Pageable pageable) {
        
        Page<PrescriptionDto> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId, pageable);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<Page<PrescriptionDto>> getPrescriptionsByDoctor(
            @PathVariable UUID doctorId,
            @PageableDefault(size = 20, sort = "prescriptionDate") Pageable pageable) {
        
        Page<PrescriptionDto> prescriptions = prescriptionService.getPrescriptionsByDoctor(doctorId, pageable);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<List<PrescriptionDto>> getPrescriptionsByConsultation(@PathVariable UUID consultationId) {
        List<PrescriptionDto> prescriptions = prescriptionService.getPrescriptionsByConsultation(consultationId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<PrescriptionDto>> getPrescriptionsByStatus(
            @PathVariable Prescription.Status status,
            @PageableDefault(size = 20, sort = "prescriptionDate") Pageable pageable) {
        
        Page<PrescriptionDto> prescriptions = prescriptionService.getPrescriptionsByStatus(status, pageable);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<PrescriptionDto>> getPrescriptionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20, sort = "prescriptionDate") Pageable pageable) {
        
        Page<PrescriptionDto> prescriptions = prescriptionService.getPrescriptionsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/today")
    public ResponseEntity<List<PrescriptionDto>> getTodaysPrescriptions() {
        List<PrescriptionDto> prescriptions = prescriptionService.getTodaysPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/active/older-than")
    public ResponseEntity<List<PrescriptionDto>> getActivePrescriptionsOlderThan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cutoffDate) {
        
        List<PrescriptionDto> prescriptions = prescriptionService.getActivePrescriptionsOlderThan(cutoffDate);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/stats/status")
    public ResponseEntity<Long> countByStatus(@RequestParam Prescription.Status status) {
        long count = prescriptionService.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/doctor/{doctorId}")
    public ResponseEntity<Long> countByDoctor(@PathVariable UUID doctorId) {
        long count = prescriptionService.countByDoctorId(doctorId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/patient/{patientId}")
    public ResponseEntity<Long> countByPatient(@PathVariable UUID patientId) {
        long count = prescriptionService.countByPatientId(patientId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/status-distribution")
    public ResponseEntity<List<Object[]>> getPrescriptionStatusStatistics() {
        List<Object[]> stats = prescriptionService.getPrescriptionStatusStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/by-date")
    public ResponseEntity<List<Object[]>> getPrescriptionStatisticsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        
        List<Object[]> stats = prescriptionService.getPrescriptionStatisticsByDate(startDate);
        return ResponseEntity.ok(stats);
    }
}