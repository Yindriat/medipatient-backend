package com.medipatient.prescription.service;

import com.medipatient.consultation.model.Consultation;
import com.medipatient.consultation.repository.ConsultationRepository;
import com.medipatient.doctor.model.Doctor;
import com.medipatient.doctor.repository.DoctorRepository;
import com.medipatient.patient.model.Patient;
import com.medipatient.patient.repository.PatientRepository;
import com.medipatient.prescription.dto.CreatePrescriptionDto;
import com.medipatient.prescription.dto.PrescriptionDto;
import com.medipatient.prescription.dto.UpdatePrescriptionDto;
import com.medipatient.prescription.mapper.PrescriptionMapper;
import com.medipatient.prescription.model.Prescription;
import com.medipatient.prescription.repository.PrescriptionRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ConsultationRepository consultationRepository;
    private final PrescriptionMapper prescriptionMapper;

    @Transactional(readOnly = true)
    public Page<PrescriptionDto> getAllPrescriptions(Pageable pageable) {
        return prescriptionRepository.findAll(pageable)
                .map(prescriptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PrescriptionDto> findWithFilters(UUID patientId, UUID doctorId, 
                                                Prescription.Status status,
                                                LocalDate dateFrom, LocalDate dateTo,
                                                Pageable pageable) {
        return prescriptionRepository.findWithFilters(patientId, doctorId, status, dateFrom, dateTo, pageable)
                .map(prescriptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PrescriptionDto> getPrescriptionsByPatient(UUID patientId, Pageable pageable) {
        return prescriptionRepository.findByPatientId(patientId, pageable)
                .map(prescriptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PrescriptionDto> getPrescriptionsByDoctor(UUID doctorId, Pageable pageable) {
        return prescriptionRepository.findByDoctorId(doctorId, pageable)
                .map(prescriptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PrescriptionDto> getPrescriptionsByStatus(Prescription.Status status, Pageable pageable) {
        return prescriptionRepository.findByStatus(status, pageable)
                .map(prescriptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PrescriptionDto> getPrescriptionsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return prescriptionRepository.findByPrescriptionDateBetween(startDate, endDate, pageable)
                .map(prescriptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PrescriptionDto> findByMedicationName(String medicationName, Pageable pageable) {
        return prescriptionRepository.findByMedicationName(medicationName, pageable)
                .map(prescriptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<PrescriptionDto> getPrescriptionById(UUID id) {
        return prescriptionRepository.findById(id)
                .map(prescriptionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionDto> getPrescriptionsByConsultation(UUID consultationId) {
        return prescriptionRepository.findByConsultationId(consultationId)
                .stream()
                .map(prescriptionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrescriptionDto> getTodaysPrescriptions() {
        return prescriptionRepository.findTodaysPrescriptions(LocalDate.now())
                .stream()
                .map(prescriptionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrescriptionDto> getActivePrescriptionsOlderThan(LocalDate cutoffDate) {
        return prescriptionRepository.findActivePrescriptionsOlderThan(cutoffDate)
                .stream()
                .map(prescriptionMapper::toDto)
                .toList();
    }

    public PrescriptionDto createPrescription(CreatePrescriptionDto createPrescriptionDto) {
        Patient patient = patientRepository.findById(createPrescriptionDto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + createPrescriptionDto.getPatientId()));

        Doctor doctor = doctorRepository.findById(createPrescriptionDto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + createPrescriptionDto.getDoctorId()));

        // Convertir les DTOs de médicaments en entités
        List<Prescription.Medication> medications = createPrescriptionDto.getMedications()
                .stream()
                .map(prescriptionMapper::medicationToEntity)
                .collect(Collectors.toList());

        Prescription prescription = prescriptionMapper.toEntity(createPrescriptionDto);
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setMedications(medications);

        // Si une consultation est associée, la lier
        if (createPrescriptionDto.getConsultationId() != null) {
            Consultation consultation = consultationRepository.findById(createPrescriptionDto.getConsultationId())
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found with id: " + createPrescriptionDto.getConsultationId()));
            prescription.setConsultation(consultation);
        }

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.info("Created new prescription with id: {}", savedPrescription.getId());
        
        return prescriptionMapper.toDto(savedPrescription);
    }

    public PrescriptionDto updatePrescription(UUID id, UpdatePrescriptionDto updatePrescriptionDto) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with id: " + id));

        // Mettre à jour les médicaments si fournis
        if (updatePrescriptionDto.getMedications() != null) {
            List<Prescription.Medication> medications = updatePrescriptionDto.getMedications()
                    .stream()
                    .map(dto -> Prescription.Medication.builder()
                            .name(dto.getName())
                            .dosage(dto.getDosage())
                            .frequency(dto.getFrequency())
                            .duration(dto.getDuration())
                            .instructions(dto.getInstructions())
                            .build())
                    .collect(Collectors.toList());
            prescription.setMedications(medications);
        }

        prescriptionMapper.updateEntityFromDto(updatePrescriptionDto, prescription);
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        
        log.info("Updated prescription with id: {}", savedPrescription.getId());
        
        return prescriptionMapper.toDto(savedPrescription);
    }

    public PrescriptionDto updatePrescriptionStatus(UUID id, Prescription.Status status) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with id: " + id));

        prescription.setStatus(status);
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        
        log.info("Updated prescription {} status to {}", id, status);
        
        return prescriptionMapper.toDto(savedPrescription);
    }

    public void deletePrescription(UUID id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with id: " + id));

        prescriptionRepository.delete(prescription);
        log.info("Deleted prescription with id: {}", id);
    }

    @Transactional(readOnly = true)
    public long countByStatus(Prescription.Status status) {
        return prescriptionRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countByDoctorId(UUID doctorId) {
        return prescriptionRepository.countByDoctorId(doctorId);
    }

    @Transactional(readOnly = true)
    public long countByPatientId(UUID patientId) {
        return prescriptionRepository.countByPatientId(patientId);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getPrescriptionStatusStatistics() {
        return prescriptionRepository.countByStatusGrouped();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getPrescriptionStatisticsByDate(LocalDate startDate) {
        return prescriptionRepository.getPrescriptionStatisticsByDate(startDate);
    }
}