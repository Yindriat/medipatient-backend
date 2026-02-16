package com.medipatient.consultation.service;

import com.medipatient.appointment.model.Appointment;
import com.medipatient.appointment.repository.AppointmentRepository;
import com.medipatient.consultation.dto.ConsultationDto;
import com.medipatient.consultation.dto.CreateConsultationDto;
import com.medipatient.consultation.dto.UpdateConsultationDto;
import com.medipatient.consultation.mapper.ConsultationMapper;
import com.medipatient.consultation.model.Consultation;
import com.medipatient.consultation.repository.ConsultationRepository;
import com.medipatient.doctor.model.Doctor;
import com.medipatient.doctor.repository.DoctorRepository;
import com.medipatient.patient.model.Patient;
import com.medipatient.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConsultationMapper consultationMapper;

    @Transactional(readOnly = true)
    public Page<ConsultationDto> getAllConsultations(Pageable pageable) {
        return consultationRepository.findAll(pageable)
                .map(consultationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ConsultationDto> findWithFilters(UUID patientId, UUID doctorId, 
                                                ZonedDateTime dateFrom, ZonedDateTime dateTo,
                                                String diagnosis, Pageable pageable) {
        return consultationRepository.findWithFilters(patientId, doctorId, dateFrom, dateTo, diagnosis, pageable)
                .map(consultationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ConsultationDto> searchByKeyword(String keyword, Pageable pageable) {
        return consultationRepository.searchByKeyword(keyword, pageable)
                .map(consultationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ConsultationDto> getConsultationsByPatient(UUID patientId, Pageable pageable) {
        return consultationRepository.findByPatientId(patientId, pageable)
                .map(consultationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ConsultationDto> getConsultationsByDoctor(UUID doctorId, Pageable pageable) {
        return consultationRepository.findByDoctorId(doctorId, pageable)
                .map(consultationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ConsultationDto> getConsultationsByDateRange(ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable) {
        return consultationRepository.findByConsultationDateBetween(startDate, endDate, pageable)
                .map(consultationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ConsultationDto> getConsultationById(UUID id) {
        return consultationRepository.findById(id)
                .map(consultationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ConsultationDto> getConsultationsByAppointment(UUID appointmentId) {
        return consultationRepository.findByAppointmentId(appointmentId)
                .stream()
                .map(consultationMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultationDto> getTodaysConsultations() {
        ZonedDateTime today = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        return consultationRepository.findTodaysConsultations(today)
                .stream()
                .map(consultationMapper::toDto)
                .toList();
    }

    public ConsultationDto createConsultation(CreateConsultationDto createConsultationDto) {
        Patient patient = patientRepository.findById(createConsultationDto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + createConsultationDto.getPatientId()));

        Doctor doctor = doctorRepository.findById(createConsultationDto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + createConsultationDto.getDoctorId()));

        Consultation consultation = consultationMapper.toEntity(createConsultationDto);
        consultation.setPatient(patient);
        consultation.setDoctor(doctor);

        // Si un rendez-vous est associé, le lier
        if (createConsultationDto.getAppointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(createConsultationDto.getAppointmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + createConsultationDto.getAppointmentId()));
            consultation.setAppointment(appointment);
            
            // Marquer le rendez-vous comme complété
            appointment.setStatus(Appointment.Status.COMPLETED);
            appointmentRepository.save(appointment);
        }

        Consultation savedConsultation = consultationRepository.save(consultation);
        log.info("Created new consultation with id: {}", savedConsultation.getId());
        
        return consultationMapper.toDto(savedConsultation);
    }

    public ConsultationDto updateConsultation(UUID id, UpdateConsultationDto updateConsultationDto) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found with id: " + id));

        consultationMapper.updateEntityFromDto(updateConsultationDto, consultation);
        Consultation savedConsultation = consultationRepository.save(consultation);
        
        log.info("Updated consultation with id: {}", savedConsultation.getId());
        
        return consultationMapper.toDto(savedConsultation);
    }

    public void deleteConsultation(UUID id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found with id: " + id));

        consultationRepository.delete(consultation);
        log.info("Deleted consultation with id: {}", id);
    }

    @Transactional(readOnly = true)
    public long countByDoctorId(UUID doctorId) {
        return consultationRepository.countByDoctorId(doctorId);
    }

    @Transactional(readOnly = true)
    public long countByPatientId(UUID patientId) {
        return consultationRepository.countByPatientId(patientId);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getConsultationStatisticsByDate(ZonedDateTime startDate) {
        return consultationRepository.getConsultationStatisticsByDate(startDate);
    }
}