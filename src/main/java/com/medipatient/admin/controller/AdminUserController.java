package com.medipatient.admin.controller;

import com.medipatient.admin.service.AdminUserService;
import com.medipatient.profile.dto.CreateProfileDto;
import com.medipatient.profile.dto.ProfileDto;
import com.medipatient.profile.dto.UpdateProfileDto;
import com.medipatient.profile.model.Profile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Administration des utilisateurs", description = "Gestion des utilisateurs par les administrateurs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    @Operation(summary = "Créer un utilisateur", description = "Crée un nouvel utilisateur (admin uniquement)")
    public ResponseEntity<ProfileDto> createUser(@Valid @RequestBody CreateProfileDto createProfileDto) {
        ProfileDto createdUser = adminUserService.createUser(createProfileDto);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Mettre à jour un utilisateur", description = "Met à jour les informations d'un utilisateur")
    public ResponseEntity<ProfileDto> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateProfileDto updateProfileDto) {
        ProfileDto updatedUser = adminUserService.updateUser(userId, updateProfileDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Supprimer un utilisateur", description = "Supprime définitivement un utilisateur")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/toggle-status")
    @Operation(summary = "Activer/désactiver un utilisateur", description = "Change le statut actif/inactif d'un utilisateur")
    public ResponseEntity<ProfileDto> toggleUserStatus(@PathVariable UUID userId) {
        ProfileDto updatedUser = adminUserService.toggleUserStatus(userId);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{userId}/reset-password")
    @Operation(summary = "Réinitialiser le mot de passe", description = "Réinitialise le mot de passe d'un utilisateur")
    public ResponseEntity<Void> resetPassword(
            @PathVariable UUID userId,
            @Parameter(description = "Nouveau mot de passe") @RequestParam String newPassword) {
        adminUserService.resetUserPassword(userId, newPassword);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/change-role")
    @Operation(summary = "Changer le rôle", description = "Change le rôle d'un utilisateur")
    public ResponseEntity<ProfileDto> changeRole(
            @PathVariable UUID userId,
            @Parameter(description = "Nouveau rôle") @RequestParam Profile.Role newRole) {
        ProfileDto updatedUser = adminUserService.changeUserRole(userId, newRole);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs", description = "Récupère la liste paginée de tous les utilisateurs")
    public ResponseEntity<Page<ProfileDto>> getAllUsers(Pageable pageable) {
        Page<ProfileDto> users = adminUserService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des utilisateurs", description = "Recherche des utilisateurs par email")
    public ResponseEntity<List<ProfileDto>> searchUsers(
            @Parameter(description = "Motif de recherche dans l'email") @RequestParam String email) {
        List<ProfileDto> users = adminUserService.searchUsersByEmail(email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/by-role/{role}")
    @Operation(summary = "Utilisateurs par rôle", description = "Récupère tous les utilisateurs d'un rôle spécifique")
    public ResponseEntity<List<ProfileDto>> getUsersByRole(@PathVariable Profile.Role role) {
        List<ProfileDto> users = adminUserService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Statistiques des utilisateurs", description = "Récupère les statistiques détaillées des utilisateurs")
    public ResponseEntity<AdminUserService.UserStatsDto> getUserStatistics() {
        AdminUserService.UserStatsDto stats = adminUserService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }

    // Endpoints pour la gestion des sessions par admin
    @GetMapping("/{userId}/sessions")
    @Operation(summary = "Sessions d'un utilisateur", description = "Récupère toutes les sessions actives d'un utilisateur")
    public ResponseEntity<Object> getUserSessions(@PathVariable UUID userId) {
        // TODO: Implémenter via AuthService
        return ResponseEntity.ok().build();
    }

   
}