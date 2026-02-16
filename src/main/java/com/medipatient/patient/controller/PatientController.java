package com.medipatient.patient.controller;

import com.medipatient.patient.dto.CreatePatientDto;
import com.medipatient.patient.dto.PatientDto;
import com.medipatient.patient.dto.UpdatePatientDto;
import com.medipatient.patient.model.Patient;
import com.medipatient.patient.service.PatientService;
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
import com.medipatient.patient.model.Gender;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Gestion des patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<Page<PatientDto>> getAllPatients(
            @PageableDefault(size = 20, sort = "user.lastName") Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String bloodType,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {
        
        Page<PatientDto> patients;
        
        if (search != null) {
            patients = patientService.searchPatients(search, pageable);
        } else if (gender != null || bloodType != null || minAge != null || maxAge != null) {
            patients = patientService.findWithFilters(gender, bloodType, minAge, maxAge, pageable);
        } else {
            patients = patientService.getAllPatients(pageable);
        }
        
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable UUID id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PatientDto> getPatientByUserId(@PathVariable UUID userId) {
        return patientService.getPatientByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody CreatePatientDto createPatientDto) {
        try {
            PatientDto createdPatient = patientService.createPatient(createPatientDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> updatePatient(@PathVariable UUID id, 
                                                   @Valid @RequestBody UpdatePatientDto updatePatientDto) {
        try {
            PatientDto updatedPatient = patientService.updatePatient(id, updatePatientDto);
            return ResponseEntity.ok(updatedPatient);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        try {
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/blood-type/{bloodType}")
    public ResponseEntity<List<PatientDto>> getPatientsByBloodType(@PathVariable String bloodType) {
        List<PatientDto> patients = patientService.getPatientsByBloodType(bloodType);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/allergy/{allergy}")
    public ResponseEntity<List<PatientDto>> getPatientsByAllergy(@PathVariable String allergy) {
        List<PatientDto> patients = patientService.getPatientsByAllergy(allergy);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/birth-date-range")
    public ResponseEntity<List<PatientDto>> getPatientsByDateOfBirthRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<PatientDto> patients = patientService.getPatientsByDateOfBirthRange(startDate, endDate);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/stats/gender")
    public ResponseEntity<Long> countByGender(@RequestParam Gender gender) {
        long count = patientService.countByGender(gender);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/blood-type")
    public ResponseEntity<List<Object[]>> getBloodTypeStatistics() {
        List<Object[]> stats = patientService.getBloodTypeStatistics();
        return ResponseEntity.ok(stats);
    }
}