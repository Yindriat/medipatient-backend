package com.medipatient.auth.controller;

import com.medipatient.auth.dto.LoginRequestDto;
import com.medipatient.auth.dto.LoginResponseDto;
import com.medipatient.auth.service.JwtAuthService;
import com.medipatient.profile.dto.ProfileDto;
import com.medipatient.profile.model.Profile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Gestion de l'authentification et autorisation")
public class AuthController {

    private final JwtAuthService jwtAuthService;

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et génère un token JWT")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto response = jwtAuthService.loginWithJwt(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion", description = "Déconnexion côté client (suppression du token)")
    public ResponseEntity<Void> logout() {
        // Avec JWT, la déconnexion se fait côté client en supprimant le token
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Utilisateur actuel", description = "Récupère les informations de l'utilisateur connecté")
    public ResponseEntity<ProfileDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        Optional<Profile> currentUser = jwtAuthService.getCurrentUser(email);
        if (currentUser.isPresent()) {
            return ResponseEntity.ok(ProfileDto.builder()
                    .id(currentUser.get().getId())
                    .firstName(currentUser.get().getFirstName())
                    .lastName(currentUser.get().getLastName())
                    .email(currentUser.get().getEmail())
                    .phone(currentUser.get().getPhone())
                    .role(currentUser.get().getRole())
                    .enabled(currentUser.get().getEnabled())
                    .createdAt(currentUser.get().getCreatedAt())
                    .updatedAt(currentUser.get().getUpdatedAt())
                    .build());
        }
        
        return ResponseEntity.notFound().build();
    }
}