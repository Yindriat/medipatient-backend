package com.medipatient.auth.service;

import com.medipatient.auth.dto.LoginRequestDto;
import com.medipatient.auth.dto.LoginResponseDto;
import com.medipatient.auth.exception.AuthenticationException;
import com.medipatient.profile.model.Profile;
import com.medipatient.profile.dto.ProfileDto;
import com.medipatient.profile.repository.ProfileRepository;
import com.medipatient.profile.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JwtAuthService {

    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ProfileMapper profileMapper;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Authentifie un utilisateur et génère un token JWT
     */
    @Transactional
    public LoginResponseDto loginWithJwt(LoginRequestDto loginRequest) {
        try {
            log.debug("Tentative de connexion pour l'utilisateur: {}", loginRequest.getEmail());

            // Validation des données d'entrée
            if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
                throw new AuthenticationException("L'email est requis");
            }
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                throw new AuthenticationException("Le mot de passe est requis");
            }

            // Authentification avec Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Récupération du profil utilisateur
            Profile profile = profileRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new AuthenticationException("Utilisateur non trouvé"));

            // Vérification que l'utilisateur est activé
            if (!profile.isEnabled()) {
                log.warn("Tentative de connexion d'un compte désactivé: {}", loginRequest.getEmail());
                throw new AuthenticationException("Compte utilisateur désactivé");
            }

            // Génération du token JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            
            // Ajout de claims personnalisés
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", profile.getId().toString());
            extraClaims.put("role", profile.getRole().toString());
            extraClaims.put("firstName", profile.getFirstName());
            extraClaims.put("lastName", profile.getLastName());
            
            String jwtToken = jwtService.generateToken(extraClaims, userDetails);

            // Calcul de l'expiration
            Date expirationDate = jwtService.extractExpiration(jwtToken);
            LocalDateTime expiresAt = expirationDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            log.info("Connexion réussie pour l'utilisateur: {} ({})", profile.getEmail(), profile.getRole());

            return LoginResponseDto.builder()
                    .sessionId(jwtToken) // Le token JWT remplace le sessionId
                    .user(ProfileDto.builder()
                            .id(profile.getId())
                            .firstName(profile.getFirstName())
                            .lastName(profile.getLastName())
                            .email(profile.getEmail())
                            .phone(profile.getPhone())
                            .role(profile.getRole())
                            .enabled(profile.getEnabled())
                            .createdAt(profile.getCreatedAt())
                            .updatedAt(profile.getUpdatedAt())
                            .build())
                    .expiresAt(expiresAt)
                    .rememberMe(false) // Non applicable avec JWT
                    .message("Connexion réussie")
                    .loginTime(LocalDateTime.now())
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Échec de connexion pour l'utilisateur: {} - Identifiants incorrects", loginRequest.getEmail());
            throw new AuthenticationException("Identifiants incorrects");
        } catch (Exception e) {
            log.error("Erreur lors de la connexion pour l'utilisateur: {} - {}", loginRequest.getEmail(), e.getMessage());
            throw new AuthenticationException("Erreur de connexion");
        }
    }

    /**
     * Récupère les informations de l'utilisateur connecté
     */
    @Transactional(readOnly = true)
    public Optional<Profile> getCurrentUser(String email) {
        return profileRepository.findByEmail(email);
    }

    /**
     * Change le mot de passe d'un utilisateur
     */
    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Utilisateur non trouvé"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, profile.getPassword())) {
            throw new AuthenticationException("Ancien mot de passe incorrect");
        }

        // Encoder et sauvegarder le nouveau mot de passe
        profile.setPassword(passwordEncoder.encode(newPassword));
        profileRepository.save(profile);

        log.info("Mot de passe modifié pour l'utilisateur: {}", email);
    }

    /**
     * Valide un token JWT
     */
    public boolean validateToken(String token) {
        try {
            return jwtService.validateToken(token);
        } catch (Exception e) {
            log.warn("Token JWT invalide: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extrait l'email/username du token
     */
    public String extractUsername(String token) {
        return jwtService.extractUsername(token);
    }
}