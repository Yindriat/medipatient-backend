package com.medipatient.appointment.service;

import com.medipatient.appointment.dto.AppointmentDto;
import com.medipatient.appointment.dto.AvailableSlotDto;
import com.medipatient.appointment.dto.CreateAppointmentDto;
import com.medipatient.appointment.dto.UpdateAppointmentDto;
import com.medipatient.appointment.mapper.AppointmentMapper;
import com.medipatient.appointment.model.Appointment;
import com.medipatient.appointment.repository.AppointmentRepository;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(appointmentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDto> findWithFilters(UUID patientId, UUID doctorId, 
                                               Appointment.Status status,
                                               Appointment.ConsultationType consultationType,
                                               LocalDate dateFrom, LocalDate dateTo,
                                               Pageable pageable) {
        return appointmentRepository.findWithFilters(patientId, doctorId, status, 
                                                    consultationType, dateFrom, dateTo, pageable)
                .map(appointmentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAppointmentsByPatient(UUID patientId, Pageable pageable) {
        return appointmentRepository.findByPatientId(patientId, pageable)
                .map(appointmentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAppointmentsByDoctor(UUID doctorId, Pageable pageable) {
        return appointmentRepository.findByDoctorId(doctorId, pageable)
                .map(appointmentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAppointmentsByDate(LocalDate date, Pageable pageable) {
        return appointmentRepository.findByAppointmentDate(date, pageable)
                .map(appointmentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAppointmentsByStatus(Appointment.Status status, Pageable pageable) {
        return appointmentRepository.findByStatus(status, pageable)
                .map(appointmentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<AppointmentDto> getAppointmentById(UUID id) {
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getTodaysConfirmedAppointments() {
        return appointmentRepository.findTodaysConfirmedAppointments(LocalDate.now())
                .stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getOverdueAppointments() {
        return appointmentRepository.findOverdueAppointments(LocalDate.now())
                .stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AvailableSlotDto> getAvailableSlots(UUID doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + doctorId));

        if (!doctor.getAvailabilityStatus().equals(Doctor.AvailabilityStatus.AVAILABLE)) {
            return new ArrayList<>();
        }

        List<Appointment> existingAppointments = appointmentRepository.findByDoctorAndDate(doctorId, date);
        List<AvailableSlotDto> availableSlots = new ArrayList<>();

        // Simple slot generation (9h-17h, créneaux de 30 minutes)
        LocalTime currentTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);

        while (currentTime.isBefore(endTime)) {
            LocalTime slotEnd = currentTime.plusMinutes(30);
            
            final LocalTime slotStartTime = currentTime;
            boolean isSlotAvailable = existingAppointments.stream()
                    .noneMatch(apt -> apt.getAppointmentTime().equals(slotStartTime) && 
                              !apt.getStatus().equals(Appointment.Status.CANCELLED));

            availableSlots.add(AvailableSlotDto.builder()
                    .doctorId(doctorId)
                    .date(date)
                    .startTime(currentTime)
                    .endTime(slotEnd)
                    .isAvailable(isSlotAvailable)
                    .build());

            currentTime = slotEnd;
        }

        return availableSlots;
    }

    public AppointmentDto createAppointment(CreateAppointmentDto createAppointmentDto) {
        Patient patient = patientRepository.findById(createAppointmentDto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + createAppointmentDto.getPatientId()));

        Doctor doctor = doctorRepository.findById(createAppointmentDto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + createAppointmentDto.getDoctorId()));

        // Vérifier les conflits
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
                createAppointmentDto.getDoctorId(),
                createAppointmentDto.getAppointmentDate(),
                createAppointmentDto.getAppointmentTime());

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Doctor is not available at this time slot");
        }

        Appointment appointment = appointmentMapper.toEntity(createAppointmentDto);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Created new appointment with id: {}", savedAppointment.getId());
        
        return appointmentMapper.toDto(savedAppointment);
    }

    public AppointmentDto updateAppointment(UUID id, UpdateAppointmentDto updateAppointmentDto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + id));

        // Si on modifie la date/heure, vérifier les conflits
        if (updateAppointmentDto.getAppointmentDate() != null || 
            updateAppointmentDto.getAppointmentTime() != null) {
            
            LocalDate newDate = updateAppointmentDto.getAppointmentDate() != null ? 
                    updateAppointmentDto.getAppointmentDate() : appointment.getAppointmentDate();
            LocalTime newTime = updateAppointmentDto.getAppointmentTime() != null ? 
                    updateAppointmentDto.getAppointmentTime() : appointment.getAppointmentTime();

            List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
                    appointment.getDoctor().getId(), newDate, newTime);
            
            // Exclure le rendez-vous actuel des conflits
            conflicts = conflicts.stream()
                    .filter(apt -> !apt.getId().equals(id))
                    .toList();

            if (!conflicts.isEmpty()) {
                throw new IllegalArgumentException("Doctor is not available at this time slot");
            }
        }

        appointmentMapper.updateEntityFromDto(updateAppointmentDto, appointment);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        log.info("Updated appointment with id: {}", savedAppointment.getId());
        
        return appointmentMapper.toDto(savedAppointment);
    }

    public AppointmentDto updateAppointmentStatus(UUID id, Appointment.Status status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + id));

        appointment.setStatus(status);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        log.info("Updated appointment {} status to {}", id, status);
        
        return appointmentMapper.toDto(savedAppointment);
    }

    public void deleteAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + id));

        appointmentRepository.delete(appointment);
        log.info("Deleted appointment with id: {}", id);
    }

    @Transactional(readOnly = true)
    public long countByStatus(Appointment.Status status) {
        return appointmentRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getConsultationTypeStatistics() {
        return appointmentRepository.countByConsultationType();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getPaymentStatusStatistics() {
        return appointmentRepository.countByPaymentStatus();
    }
}