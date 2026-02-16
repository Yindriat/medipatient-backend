package com.medipatient.admin.service;

import com.medipatient.auth.service.JwtAuthService;
import com.medipatient.profile.dto.CreateProfileDto;
import com.medipatient.profile.dto.ProfileDto;
import com.medipatient.profile.dto.UpdateProfileDto;
import com.medipatient.profile.model.Profile;
import com.medipatient.profile.repository.ProfileRepository;
import com.medipatient.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminUserService {

    private final ProfileRepository profileRepository;
    private final ProfileService profileService;
    private final JwtAuthService authService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crée un nouvel utilisateur (admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ProfileDto createUser(CreateProfileDto createProfileDto) {
        log.info("Admin creating new user: {}", createProfileDto.getEmail());
        
        // Utiliser le service existant qui gère déjà le cryptage
        ProfileDto createdUser = profileService.createProfile(createProfileDto);
        
        log.info("User {} created successfully by admin", createdUser.getEmail());
        return createdUser;
    }

    /**
     * Met à jour un utilisateur (admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ProfileDto updateUser(UUID userId, UpdateProfileDto updateProfileDto) {
        log.info("Admin updating user: {}", userId);
        
        ProfileDto updatedUser = profileService.updateProfile(userId, updateProfileDto);
        
        log.info("User {} updated successfully by admin", updatedUser.getEmail());
        return updatedUser;
    }

    /**
     * Supprime un utilisateur (admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUser(UUID userId) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Empêcher la suppression du dernier admin
        if (profile.getRole() == Profile.Role.ADMIN) {
            long adminCount = profileRepository.countByRoleAndEnabledTrue(Profile.Role.ADMIN);
            if (adminCount <= 1) {
                throw new IllegalStateException("Cannot delete the last admin user");
            }
        }

        // Supprimer l'utilisateur
        profileRepository.delete(profile);
        
        log.info("User {} ({}) deleted successfully by admin", profile.getEmail(), userId);
    }

    /**
     * Active/désactive un utilisateur (admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ProfileDto toggleUserStatus(UUID userId) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        boolean newStatus = !profile.getEnabled();
        
        // Empêcher la désactivation du dernier admin
        if (profile.getRole() == Profile.Role.ADMIN && !newStatus) {
            long activeAdminCount = profileRepository.countByRoleAndEnabledTrue(Profile.Role.ADMIN);
            if (activeAdminCount <= 1) {
                throw new IllegalStateException("Cannot disable the last active admin user");
            }
        }

        profile.setEnabled(newStatus);
        Profile savedProfile = profileRepository.save(profile);

        // Avec JWT, pas besoin d'invalider les sessions

        log.info("User {} ({}) {} by admin", profile.getEmail(), userId, 
                newStatus ? "enabled" : "disabled");

        return convertToProfileDto(savedProfile);
    }

    /**
     * Réinitialise le mot de passe d'un utilisateur (admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void resetUserPassword(UUID userId, String newPassword) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Encoder le nouveau mot de passe
        profile.setPassword(passwordEncoder.encode(newPassword));
        profileRepository.save(profile);

        // Avec JWT, le token reste valide jusqu'à expiration

        log.info("Password reset for user {} ({}) by admin", profile.getEmail(), userId);
    }

    /**
     * Change le rôle d'un utilisateur (admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ProfileDto changeUserRole(UUID userId, Profile.Role newRole) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Profile.Role oldRole = profile.getRole();

        // Empêcher la modification du rôle du dernier admin
        if (oldRole == Profile.Role.ADMIN && newRole != Profile.Role.ADMIN) {
            long adminCount = profileRepository.countByRoleAndEnabledTrue(Profile.Role.ADMIN);
            if (adminCount <= 1) {
                throw new IllegalStateException("Cannot change role of the last admin user");
            }
        }

        profile.setRole(newRole);
        Profile savedProfile = profileRepository.save(profile);

        // Avec JWT, les nouveaux droits s'appliquent au prochain login

        log.info("User {} ({}) role changed from {} to {} by admin", 
                profile.getEmail(), userId, oldRole, newRole);

        return convertToProfileDto(savedProfile);
    }

    /**
     * Récupère tous les utilisateurs avec pagination (admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<ProfileDto> getAllUsers(Pageable pageable) {
        return profileRepository.findAll(pageable)
                .map(this::convertToProfileDto);
    }

    /**
     * Recherche d'utilisateurs par email (admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<ProfileDto> searchUsersByEmail(String emailPattern) {
        List<Profile> profiles = profileRepository.findByEmailContainingIgnoreCase(emailPattern);
        return profiles.stream()
                .map(this::convertToProfileDto)
                .toList();
    }

    /**
     * Récupère les utilisateurs par rôle (admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<ProfileDto> getUsersByRole(Profile.Role role) {
        List<Profile> profiles = profileRepository.findByRole(role);
        return profiles.stream()
                .map(this::convertToProfileDto)
                .toList();
    }

    // Méthode supprimée: logoutAllUserSessions - non applicable avec JWT

    /**
     * Statistiques des utilisateurs (admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public UserStatsDto getUserStatistics() {
        long totalUsers = profileRepository.count();
        long activeUsers = profileRepository.countByEnabledTrue();
        long adminUsers = profileRepository.countByRole(Profile.Role.ADMIN);
        long doctorUsers = profileRepository.countByRole(Profile.Role.DOCTOR);
        long patientUsers = profileRepository.countByRole(Profile.Role.PATIENT);
        long agentUsers = profileRepository.countByRole(Profile.Role.AGENT);

        return UserStatsDto.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(totalUsers - activeUsers)
                .adminUsers(adminUsers)
                .doctorUsers(doctorUsers)
                .patientUsers(patientUsers)
                .agentUsers(agentUsers)
                .build();
    }

    // DTO pour les statistiques
    public static record UserStatsDto(
        long totalUsers,
        long activeUsers,
        long inactiveUsers,
        long adminUsers,
        long doctorUsers,
        long patientUsers,
        long agentUsers
    ) {
        public static UserStatsDtoBuilder builder() {
            return new UserStatsDtoBuilder();
        }

        public static class UserStatsDtoBuilder {
            private long totalUsers;
            private long activeUsers;
            private long inactiveUsers;
            private long adminUsers;
            private long doctorUsers;
            private long patientUsers;
            private long agentUsers;

            public UserStatsDtoBuilder totalUsers(long totalUsers) {
                this.totalUsers = totalUsers;
                return this;
            }

            public UserStatsDtoBuilder activeUsers(long activeUsers) {
                this.activeUsers = activeUsers;
                return this;
            }

            public UserStatsDtoBuilder inactiveUsers(long inactiveUsers) {
                this.inactiveUsers = inactiveUsers;
                return this;
            }

            public UserStatsDtoBuilder adminUsers(long adminUsers) {
                this.adminUsers = adminUsers;
                return this;
            }

            public UserStatsDtoBuilder doctorUsers(long doctorUsers) {
                this.doctorUsers = doctorUsers;
                return this;
            }

            public UserStatsDtoBuilder patientUsers(long patientUsers) {
                this.patientUsers = patientUsers;
                return this;
            }

            public UserStatsDtoBuilder agentUsers(long agentUsers) {
                this.agentUsers = agentUsers;
                return this;
            }

            public UserStatsDto build() {
                return new UserStatsDto(totalUsers, activeUsers, inactiveUsers, 
                                      adminUsers, doctorUsers, patientUsers, agentUsers);
            }
        }
    }

    // Méthode utilitaire pour convertir Profile en ProfileDto
    private ProfileDto convertToProfileDto(Profile profile) {
        return ProfileDto.builder()
                .id(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .role(profile.getRole())
                .enabled(profile.getEnabled())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}