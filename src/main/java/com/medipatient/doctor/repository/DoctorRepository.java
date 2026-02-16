package com.medipatient.doctor.repository;

import com.medipatient.doctor.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Optional<Doctor> findByUserId(UUID userId);

    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Doctor> findBySpecialtyId(UUID specialtyId);

    Page<Doctor> findBySpecialtyId(UUID specialtyId, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.availabilityStatus = :status")
    List<Doctor> findByAvailabilityStatus(@Param("status") Doctor.AvailabilityStatus status);

    @Query("SELECT d FROM Doctor d WHERE d.availabilityStatus = :status")
    Page<Doctor> findByAvailabilityStatus(@Param("status") Doctor.AvailabilityStatus status, Pageable pageable);

    @Query("SELECT d FROM Doctor d JOIN d.user u WHERE " +
           "(:search IS NULL OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:specialtyId IS NULL OR d.specialty.id = :specialtyId) AND " +
           "(:availabilityStatus IS NULL OR d.availabilityStatus = :availabilityStatus)")
    Page<Doctor> searchDoctors(@Param("search") String search,
                               @Param("specialtyId") UUID specialtyId,
                               @Param("availabilityStatus") Doctor.AvailabilityStatus availabilityStatus,
                               Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE " +
           "d.availabilityStatus = 'AVAILABLE' AND " +
           "d.id NOT IN (" +
           "SELECT a.doctor.id FROM Appointment a " +
           "WHERE a.appointmentDate = :date AND a.status NOT IN ('CANCELLED', 'COMPLETED')" +
           ")")
    List<Doctor> findAvailableDoctorsForDate(@Param("date") java.time.LocalDate date);

    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.availabilityStatus = :status")
    long countByAvailabilityStatus(@Param("status") Doctor.AvailabilityStatus status);

    @Query("SELECT s.name, COUNT(d) FROM Doctor d JOIN d.specialty s GROUP BY s.name")
    List<Object[]> countBySpecialty();

    @Query("SELECT d FROM Doctor d WHERE d.consultationFee BETWEEN :minFee AND :maxFee")
    Page<Doctor> findByConsultationFeeBetween(@Param("minFee") Integer minFee, 
                                              @Param("maxFee") Integer maxFee, 
                                              Pageable pageable);
}