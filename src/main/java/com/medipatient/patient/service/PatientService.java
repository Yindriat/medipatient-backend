package com.medipatient.patient.service;

import com.medipatient.patient.dto.CreatePatientDto;
import com.medipatient.patient.dto.PatientDto;
import com.medipatient.patient.dto.UpdatePatientDto;
import com.medipatient.patient.mapper.PatientMapper;
import com.medipatient.patient.model.Patient;
import com.medipatient.patient.repository.PatientRepository;
import com.medipatient.profile.model.Profile;
import com.medipatient.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
...existing code...
        
        return patientMapper.toDto(savedPatient);
    }

    public void deletePatient(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + id));

        patientRepository.delete(patient);
        log.info("Deleted patient with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<PatientDto> getPatientsByBloodType(String bloodType) {
        return patientRepository.findByBloodType(bloodType)
                .stream()
                .map(patientMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PatientDto> getPatientsByAllergy(String allergy) {
        return patientRepository.findByAllergy(allergy)
                .stream()
                .map(patientMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PatientDto> getPatientsByDateOfBirthRange(LocalDate startDate, LocalDate endDate) {
        return patientRepository.findByDateOfBirthBetween(startDate, endDate)
                .stream()
                .map(patientMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countByGender(Patient.Gender gender) {
        return patientRepository.countByGender(gender);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getBloodTypeStatistics() {
        return patientRepository.countByBloodType();
    }
}