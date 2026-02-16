package com.medipatient.appointment.repository;

import com.medipatient.appointment.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByPatientId(UUID patientId);

    Page<Appointment> findByPatientId(UUID patientId, Pageable pageable);

    List<Appointment> findByDoctorId(UUID doctorId);

    Page<Appointment> findByDoctorId(UUID doctorId, Pageable pageable);

    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    Page<Appointment> findByAppointmentDate(LocalDate appointmentDate, Pageable pageable);

    List<Appointment> findByStatus(Appointment.Status status);

    Page<Appointment> findByStatus(Appointment.Status status, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE " +
           "a.doctor.id = :doctorId AND " +
           "a.appointmentDate = :date AND " +
           "a.status NOT IN ('CANCELLED', 'COMPLETED')")
    List<Appointment> findByDoctorAndDate(@Param("doctorId") UUID doctorId, 
                                          @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE " +
           "a.doctor.id = :doctorId AND " +
           "a.appointmentDate = :date AND " +
           "a.appointmentTime = :time AND " +
           "a.status NOT IN ('CANCELLED', 'COMPLETED')")
    List<Appointment> findConflictingAppointments(@Param("doctorId") UUID doctorId,
                                                   @Param("date") LocalDate date,
                                                   @Param("time") LocalTime time);

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:patientId IS NULL OR a.patient.id = :patientId) AND " +
           "(:doctorId IS NULL OR a.doctor.id = :doctorId) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:consultationType IS NULL OR a.consultationType = :consultationType) AND " +
           "(:dateFrom IS NULL OR a.appointmentDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR a.appointmentDate <= :dateTo)")
    Page<Appointment> findWithFilters(@Param("patientId") UUID patientId,
                                      @Param("doctorId") UUID doctorId,
                                      @Param("status") Appointment.Status status,
                                      @Param("consultationType") Appointment.ConsultationType consultationType,
                                      @Param("dateFrom") LocalDate dateFrom,
                                      @Param("dateTo") LocalDate dateTo,
                                      Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE " +
           "a.appointmentDate BETWEEN :startDate AND :endDate AND " +
           "a.status = :status")
    List<Appointment> findByDateRangeAndStatus(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               @Param("status") Appointment.Status status);

    @Query("SELECT a FROM Appointment a WHERE " +
           "a.appointmentDate = :today AND " +
           "a.status = 'CONFIRMED'")
    List<Appointment> findTodaysConfirmedAppointments(@Param("today") LocalDate today);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
    long countByStatus(@Param("status") Appointment.Status status);

    @Query("SELECT a.consultationType, COUNT(a) FROM Appointment a GROUP BY a.consultationType")
    List<Object[]> countByConsultationType();

    @Query("SELECT a.paymentStatus, COUNT(a) FROM Appointment a GROUP BY a.paymentStatus")
    List<Object[]> countByPaymentStatus();

    @Query("SELECT a FROM Appointment a WHERE " +
           "a.appointmentDate < :date AND " +
           "a.status IN ('PENDING', 'CONFIRMED')")
    List<Appointment> findOverdueAppointments(@Param("date") LocalDate date);
}