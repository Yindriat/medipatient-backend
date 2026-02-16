package com.medipatient.profile.controller;

import com.medipatient.profile.dto.*;
import com.medipatient.profile.model.Profile;
import com.medipatient.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Tag(name = "Profils", description = "Gestion des profils utilisateurs")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "Récupérer tous les profils", 
               description = "Récupère la liste paginée de tous les profils avec possibilité de filtrage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des profils récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Paramètres de requête invalides"),
        @ApiResponse(responseCode = "401", description = "Non autorisé")
    })
    public ResponseEntity<Page<ProfileDto>> getAllProfiles(
            @Parameter(description = "Paramètres de pagination") @PageableDefault(size = 20, sort = "lastName") Pageable pageable,
            @Parameter(description = "Filtrer par rôle") @RequestParam(required = false) Profile.Role role,
            @Parameter(description = "Filtrer par statut actif") @RequestParam(required = false) Boolean enabled,
            @Parameter(description = "Recherche par nom ou email") @RequestParam(required = false) String search) {
        
        Page<ProfileDto> profiles = profileService.searchProfiles(search, role, enabled, pageable);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getProfileById(@PathVariable UUID id) {
        return profileService.getProfileById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ProfileDto> getProfileByEmail(@PathVariable String email) {
        return profileService.getProfileByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProfileDto> createProfile(@Valid @RequestBody CreateProfileDto createProfileDto) {
        try {
            ProfileDto createdProfile = profileService.createProfile(createProfileDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileDto> updateProfile(@PathVariable UUID id, 
                                                   @Valid @RequestBody UpdateProfileDto updateProfileDto) {
        try {
            ProfileDto updatedProfile = profileService.updateProfile(id, updateProfileDto);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable UUID id) {
        try {
            profileService.deleteProfile(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<Page<ProfileDto>> getProfilesByRole(
            @PathVariable Profile.Role role,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        
        Page<ProfileDto> profiles = profileService.getProfilesByRole(role, pageable);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/stats")
    public ResponseEntity<ProfileStatsDto> getProfileStats() {
        ProfileStatsDto stats = profileService.getProfileStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<EmailCheckDto> checkEmailExists(@PathVariable String email) {
        boolean exists = profileService.existsByEmail(email);
        EmailCheckDto response = EmailCheckDto.builder()
                .exists(exists)
                .email(email)
                .build();
        return ResponseEntity.ok(response);
    }
}