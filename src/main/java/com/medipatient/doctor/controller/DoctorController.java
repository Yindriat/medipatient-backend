package com.medipatient.doctor.controller;

import com.medipatient.doctor.dto.CreateDoctorDto;
import com.medipatient.doctor.dto.DoctorDto;
import com.medipatient.doctor.dto.UpdateDoctorDto;
import com.medipatient.doctor.model.Doctor;
import com.medipatient.doctor.service.DoctorService;
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
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Médecins", description = "Gestion des médecins")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<Page<DoctorDto>> getAllDoctors(
            @PageableDefault(size = 20, sort = "user.lastName") Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID specialtyId,
            @RequestParam(required = false) Doctor.AvailabilityStatus availabilityStatus,
            @RequestParam(required = false) Integer minFee,
            @RequestParam(required = false) Integer maxFee) {
        
        Page<DoctorDto> doctors;
        
        if (search != null || specialtyId != null || availabilityStatus != null) {
            doctors = doctorService.searchDoctors(search, specialtyId, availabilityStatus, pageable);
        } else if (minFee != null || maxFee != null) {
            doctors = doctorService.getDoctorsByConsultationFeeRange(
                minFee != null ? minFee : 0, 
                maxFee != null ? maxFee : Integer.MAX_VALUE, 
                pageable);
        } else {
            doctors = doctorService.getAllDoctors(pageable);
        }
        
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable UUID id) {
        return doctorService.getDoctorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<DoctorDto> getDoctorByUserId(@PathVariable UUID userId) {
        return doctorService.getDoctorByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/license/{licenseNumber}")
    public ResponseEntity<DoctorDto> getDoctorByLicenseNumber(@PathVariable String licenseNumber) {
        return doctorService.getDoctorByLicenseNumber(licenseNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DoctorDto> createDoctor(@Valid @RequestBody CreateDoctorDto createDoctorDto) {
        try {
            DoctorDto createdDoctor = doctorService.createDoctor(createDoctorDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDoctor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable UUID id, 
                                                 @Valid @RequestBody UpdateDoctorDto updateDoctorDto) {
        try {
            DoctorDto updatedDoctor = doctorService.updateDoctor(id, updateDoctorDto);
            return ResponseEntity.ok(updatedDoctor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<DoctorDto> updateAvailabilityStatus(@PathVariable UUID id, 
                                                             @RequestParam Doctor.AvailabilityStatus status) {
        try {
            DoctorDto updatedDoctor = doctorService.updateAvailabilityStatus(id, status);
            return ResponseEntity.ok(updatedDoctor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        try {
            doctorService.deleteDoctor(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/specialty/{specialtyId}")
    public ResponseEntity<Page<DoctorDto>> getDoctorsBySpecialty(
            @PathVariable UUID specialtyId,
            @PageableDefault(size = 20, sort = "user.lastName") Pageable pageable) {
        
        Page<DoctorDto> doctors = doctorService.getDoctorsBySpecialty(specialtyId, pageable);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DoctorDto>> getAvailableDoctorsForDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<DoctorDto> doctors = doctorService.getAvailableDoctorsForDate(date);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/availability/{status}")
    public ResponseEntity<Page<DoctorDto>> getDoctorsByAvailabilityStatus(
            @PathVariable Doctor.AvailabilityStatus status,
            @PageableDefault(size = 20, sort = "user.lastName") Pageable pageable) {
        
        Page<DoctorDto> doctors = doctorService.getDoctorsByAvailabilityStatus(status, pageable);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/stats/availability")
    public ResponseEntity<Long> countByAvailabilityStatus(@RequestParam Doctor.AvailabilityStatus status) {
        long count = doctorService.countByAvailabilityStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/specialty")
    public ResponseEntity<List<Object[]>> getSpecialtyStatistics() {
        List<Object[]> stats = doctorService.getSpecialtyStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/check-license/{licenseNumber}")
    public ResponseEntity<Boolean> checkLicenseExists(@PathVariable String licenseNumber) {
        boolean exists = doctorService.existsByLicenseNumber(licenseNumber);
        return ResponseEntity.ok(exists);
    }
}