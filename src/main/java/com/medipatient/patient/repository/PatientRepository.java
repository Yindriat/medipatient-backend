package com.medipatient.patient.repository;

import com.medipatient.patient.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.medipatient.patient.model.Gender;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByUserId(UUID userId);

    @Query("SELECT p FROM Patient p JOIN p.user u WHERE " +
           "(:search IS NULL OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Patient> searchPatients(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE " +
           "(:gender IS NULL OR p.gender = :gender) AND " +
           "(:bloodType IS NULL OR p.bloodType = :bloodType) AND " +
           "(:minAge IS NULL OR :maxAge IS NULL OR " +
           "p.dateOfBirth BETWEEN :minBirthDate AND :maxBirthDate)")
    Page<Patient> findWithFilters(@Param("gender") Gender gender,
                                  @Param("bloodType") String bloodType,
                                  @Param("minAge") Integer minAge,
                                  @Param("maxAge") Integer maxAge,
                                  @Param("minBirthDate") LocalDate minBirthDate,
                                  @Param("maxBirthDate") LocalDate maxBirthDate,
                                  Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Patient> findByDateOfBirthBetween(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Patient p WHERE p.bloodType = :bloodType")
    List<Patient> findByBloodType(@Param("bloodType") String bloodType);

    @Query(value = "SELECT * FROM patients p WHERE :allergy = ANY(p.allergies)", nativeQuery = true)
    List<Patient> findByAllergy(@Param("allergy") String allergy);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.gender = :gender")
    long countByGender(@Param("gender") Gender gender);

    @Query("SELECT p.bloodType, COUNT(p) FROM Patient p WHERE p.bloodType IS NOT NULL GROUP BY p.bloodType")
    List<Object[]> countByBloodType();
}