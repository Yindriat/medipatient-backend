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
import java.util.UUID;
import com.medipatient.patient.model.Gender;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final ProfileRepository profileRepository;
    private final PatientMapper patientMapper;

    @Transactional(readOnly = true)
    public Page<PatientDto> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable)
                .map(patientMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PatientDto> searchPatients(String search, Pageable pageable) {
        return patientRepository.searchPatients(search, pageable)
                .map(patientMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PatientDto> findWithFilters(Gender gender, String bloodType,
                                           Integer minAge, Integer maxAge, Pageable pageable) {
        LocalDate minBirthDate = null;
        LocalDate maxBirthDate = null;
        
        if (minAge != null) {
            maxBirthDate = LocalDate.now().minusYears(minAge);
        }
        if (maxAge != null) {
            minBirthDate = LocalDate.now().minusYears(maxAge + 1);
        }
        
        return patientRepository.findWithFilters(gender, bloodType, minAge, maxAge, 
                                                minBirthDate, maxBirthDate, pageable)
                .map(patientMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<PatientDto> getPatientById(UUID id) {
        return patientRepository.findById(id)
                .map(patientMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<PatientDto> getPatientByUserId(UUID userId) {
        return patientRepository.findByUserId(userId)
                .map(patientMapper::toDto);
    }

    public PatientDto createPatient(CreatePatientDto createPatientDto) {
        Profile user = profileRepository.findById(createPatientDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with id: " + createPatientDto.getUserId()));

        if (!user.getRole().equals(Profile.Role.PATIENT)) {
            throw new IllegalArgumentException("Profile must have PATIENT role to create patient record");
        }

        if (patientRepository.findByUserId(createPatientDto.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Patient record already exists for this user");
        }

        Patient patient = patientMapper.toEntity(createPatientDto);
        patient.setUser(user);
        
        Patient savedPatient = patientRepository.save(patient);
        log.info("Created new patient with id: {}", savedPatient.getId());
        
        return patientMapper.toDto(savedPatient);
    }


    public PatientDto updatePatient(UUID id, UpdatePatientDto updatePatientDto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + id));

        patientMapper.updateEntityFromDto(updatePatientDto, patient);
        Patient savedPatient = patientRepository.save(patient);
        
        log.info("Updated patient with id: {}", savedPatient.getId());
        
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
    public long countByGender(Gender gender) {
        return patientRepository.countByGender(gender);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getBloodTypeStatistics() {
        return patientRepository.countByBloodType();
    }
}