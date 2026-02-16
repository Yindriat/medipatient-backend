package com.medipatient.profile.repository;

import com.medipatient.profile.model.Profile;
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
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Profile> findByRole(Profile.Role role);

    Page<Profile> findByRole(Profile.Role role, Pageable pageable);

    @Query("SELECT p FROM Profile p WHERE p.role = :role AND p.enabled = true")
    Page<Profile> findActiveByRole(@Param("role") Profile.Role role, Pageable pageable);

    @Query("SELECT p FROM Profile p WHERE " +
           "(:search IS NULL OR " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Profile> searchProfiles(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Profile p WHERE " +
           "(:role IS NULL OR p.role = :role) AND " +
           "(:enabled IS NULL OR p.enabled = :enabled) AND " +
           "(:search IS NULL OR " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Profile> findWithFilters(@Param("role") Profile.Role role, 
                                  @Param("enabled") Boolean enabled,
                                  @Param("search") String search, 
                                  Pageable pageable);

    long countByRole(Profile.Role role);
    
    long countByRoleAndEnabledTrue(Profile.Role role);
    
    long countByEnabledTrue();

    List<Profile> findByEmailContainingIgnoreCase(String emailPattern);

    @Query("SELECT COUNT(p) FROM Profile p WHERE p.enabled = true")
    long countActiveProfiles();
}