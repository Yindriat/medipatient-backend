package com.medipatient.consultation.repository;

import com.medipatient.consultation.model.Consultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, UUID> {

    List<Consultation> findByPatientId(UUID patientId);

    Page<Consultation> findByPatientId(UUID patientId, Pageable pageable);

    List<Consultation> findByDoctorId(UUID doctorId);

    Page<Consultation> findByDoctorId(UUID doctorId, Pageable pageable);

    List<Consultation> findByAppointmentId(UUID appointmentId);

    @Query("SELECT c FROM Consultation c WHERE " +
           "c.consultationDate BETWEEN :startDate AND :endDate")
    List<Consultation> findByConsultationDateBetween(@Param("startDate") ZonedDateTime startDate,
                                                     @Param("endDate") ZonedDateTime endDate);

    @Query("SELECT c FROM Consultation c WHERE " +
           "c.consultationDate BETWEEN :startDate AND :endDate")
    Page<Consultation> findByConsultationDateBetween(@Param("startDate") ZonedDateTime startDate,
                                                     @Param("endDate") ZonedDateTime endDate,
                                                     Pageable pageable);

    @Query("SELECT c FROM Consultation c WHERE " +
           "(:patientId IS NULL OR c.patient.id = :patientId) AND " +
           "(:doctorId IS NULL OR c.doctor.id = :doctorId) AND " +
           "(:dateFrom IS NULL OR c.consultationDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR c.consultationDate <= :dateTo) AND " +
           "(:diagnosis IS NULL OR LOWER(c.diagnosis) LIKE LOWER(CONCAT('%', :diagnosis, '%')))")
    Page<Consultation> findWithFilters(@Param("patientId") UUID patientId,
                                       @Param("doctorId") UUID doctorId,
                                       @Param("dateFrom") ZonedDateTime dateFrom,
                                       @Param("dateTo") ZonedDateTime dateTo,
                                       @Param("diagnosis") String diagnosis,
                                       Pageable pageable);

    @Query("SELECT c FROM Consultation c WHERE " +
           "LOWER(c.symptoms) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.diagnosis) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.treatmentPlan) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Consultation> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.doctor.id = :doctorId")
    long countByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.patient.id = :patientId")
    long countByPatientId(@Param("patientId") UUID patientId);

    @Query("SELECT DATE(c.consultationDate), COUNT(c) FROM Consultation c " +
           "WHERE c.consultationDate >= :startDate " +
           "GROUP BY DATE(c.consultationDate) " +
           "ORDER BY DATE(c.consultationDate)")
    List<Object[]> getConsultationStatisticsByDate(@Param("startDate") ZonedDateTime startDate);

    @Query("SELECT c FROM Consultation c WHERE " +
           "c.consultationDate >= :today " +
           "ORDER BY c.consultationDate ASC")
    List<Consultation> findTodaysConsultations(@Param("today") ZonedDateTime today);
}