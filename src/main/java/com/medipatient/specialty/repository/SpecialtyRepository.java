package com.medipatient.specialty.repository;

import com.medipatient.specialty.model.Specialty;
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
public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {

    Optional<Specialty> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT s FROM Specialty s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Specialty> searchSpecialties(@Param("search") String search, Pageable pageable);

    @Query("SELECT s FROM Specialty s ORDER BY s.name")
    List<Specialty> findAllOrderByName();

    @Query("SELECT s FROM Specialty s ORDER BY s.name")
    Page<Specialty> findAllOrderByName(Pageable pageable);
}