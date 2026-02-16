package com.medipatient.prescription.repository;

import com.medipatient.prescription.model.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    List<Prescription> findByPatientId(UUID patientId);

    Page<Prescription> findByPatientId(UUID patientId, Pageable pageable);

    List<Prescription> findByDoctorId(UUID doctorId);

    Page<Prescription> findByDoctorId(UUID doctorId, Pageable pageable);

    List<Prescription> findByConsultationId(UUID consultationId);

    List<Prescription> findByStatus(Prescription.Status status);

    Page<Prescription> findByStatus(Prescription.Status status, Pageable pageable);

    @Query("SELECT p FROM Prescription p WHERE " +
           "p.prescriptionDate BETWEEN :startDate AND :endDate")
    Page<Prescription> findByPrescriptionDateBetween(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate,
                                                     Pageable pageable);

    @Query("SELECT p FROM Prescription p WHERE " +
           "(:patientId IS NULL OR p.patient.id = :patientId) AND " +
           "(:doctorId IS NULL OR p.doctor.id = :doctorId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:dateFrom IS NULL OR p.prescriptionDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR p.prescriptionDate <= :dateTo)")
    Page<Prescription> findWithFilters(@Param("patientId") UUID patientId,
                                       @Param("doctorId") UUID doctorId,
                                       @Param("status") Prescription.Status status,
                                       @Param("dateFrom") LocalDate dateFrom,
                                       @Param("dateTo") LocalDate dateTo,
                                       Pageable pageable);

    @Query(value = "SELECT * FROM prescriptions p WHERE " +
           "p.medications::text ILIKE CONCAT('%', :medicationName, '%')", nativeQuery = true)
    Page<Prescription> findByMedicationName(@Param("medicationName") String medicationName, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.status = :status")
    long countByStatus(@Param("status") Prescription.Status status);

    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.doctor.id = :doctorId")
    long countByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.patient.id = :patientId")
    long countByPatientId(@Param("patientId") UUID patientId);

    @Query("SELECT p.status, COUNT(p) FROM Prescription p GROUP BY p.status")
    List<Object[]> countByStatusGrouped();

    @Query("SELECT DATE(p.prescriptionDate), COUNT(p) FROM Prescription p " +
           "WHERE p.prescriptionDate >= :startDate " +
           "GROUP BY DATE(p.prescriptionDate) " +
           "ORDER BY DATE(p.prescriptionDate)")
    List<Object[]> getPrescriptionStatisticsByDate(@Param("startDate") LocalDate startDate);

    @Query("SELECT p FROM Prescription p WHERE " +
           "p.status = 'ACTIVE' AND " +
           "p.prescriptionDate <= :cutoffDate " +
           "ORDER BY p.prescriptionDate ASC")
    List<Prescription> findActivePrescriptionsOlderThan(@Param("cutoffDate") LocalDate cutoffDate);

    @Query("SELECT p FROM Prescription p WHERE " +
           "p.prescriptionDate = :today")
    List<Prescription> findTodaysPrescriptions(@Param("today") LocalDate today);
}