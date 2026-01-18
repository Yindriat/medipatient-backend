package com.medipatient.auth.service;

import com.medipatient.auth.dto.LoginRequestDto;
import com.medipatient.auth.dto.LoginResponseDto;
import com.medipatient.auth.dto.RegisterRequestDto;
import com.medipatient.auth.exception.AuthenticationException;
import com.medipatient.patient.model.Patient;
import com.medipatient.patient.repository.PatientRepository;
import com.medipatient.profile.model.Profile;
import com.medipatient.profile.dto.ProfileDto;
import com.medipatient.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
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
            authenticationManager.authenticate(
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
            log.warn("Échec d'authentification pour l'utilisateur: {}", loginRequest.getEmail());
            throw new AuthenticationException("Email ou mot de passe incorrect");
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la connexion: {}", e.getMessage(), e);
            throw new AuthenticationException("Erreur lors de la connexion");
        }
    }

    /**
     * Inscrit un nouveau patient et génère un token JWT
     */
    @Transactional
    public LoginResponseDto registerPatient(RegisterRequestDto registerRequest) {
        try {
            log.debug("Tentative d'inscription pour: {}", registerRequest.getEmail());

            // Validation des données d'entrée
            if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
                throw new AuthenticationException("L'email est requis");
            }
            if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
                throw new AuthenticationException("Le mot de passe est requis");
            }
            if (registerRequest.getFirstName() == null || registerRequest.getFirstName().trim().isEmpty()) {
                throw new AuthenticationException("Le prénom est requis");
            }
            if (registerRequest.getLastName() == null || registerRequest.getLastName().trim().isEmpty()) {
                throw new AuthenticationException("Le nom est requis");
            }

            // Vérification que l'email n'existe pas déjà
            if (profileRepository.findByEmail(registerRequest.getEmail().toLowerCase().trim()).isPresent()) {
                log.warn("Tentative d'inscription avec un email déjà existant: {}", registerRequest.getEmail());
                throw new AuthenticationException("Un compte avec cet email existe déjà");
            }

            // Création du profil utilisateur
            Profile profile = Profile.builder()
                    .firstName(registerRequest.getFirstName().trim())
                    .lastName(registerRequest.getLastName().trim())
                    .email(registerRequest.getEmail().toLowerCase().trim())
                    .phone(registerRequest.getPhone() != null ? registerRequest.getPhone().trim() : null)
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(Profile.Role.PATIENT)
                    .enabled(true)
                    .build();

            Profile savedProfile = profileRepository.save(profile);
            log.info("Profil créé avec succès pour: {} (ID: {})", savedProfile.getEmail(), savedProfile.getId());

            // Création de l'enregistrement patient associé
            Patient patient = Patient.builder()
                    .user(savedProfile)
                    .build();

            Patient savedPatient = patientRepository.save(patient);
            log.info("Enregistrement patient créé avec succès (ID: {})", savedPatient.getId());

            // Génération du token JWT pour auto-login après inscription
            UserDetails userDetails = userDetailsService.loadUserByUsername(savedProfile.getEmail());

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", savedProfile.getId().toString());
            extraClaims.put("role", savedProfile.getRole().toString());
            extraClaims.put("firstName", savedProfile.getFirstName());
            extraClaims.put("lastName", savedProfile.getLastName());
            extraClaims.put("patientId", savedPatient.getId().toString());

            String jwtToken = jwtService.generateToken(extraClaims, userDetails);

            // Calcul de l'expiration
            Date expirationDate = jwtService.extractExpiration(jwtToken);
            LocalDateTime expiresAt = expirationDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            log.info("Inscription réussie pour: {} ({})", savedProfile.getEmail(), savedProfile.getRole());

            return LoginResponseDto.builder()
                    .sessionId(jwtToken)
                    .user(ProfileDto.builder()
                            .id(savedProfile.getId())
                            .firstName(savedProfile.getFirstName())
                            .lastName(savedProfile.getLastName())
                            .email(savedProfile.getEmail())
                            .phone(savedProfile.getPhone())
                            .role(savedProfile.getRole())
                            .enabled(savedProfile.getEnabled())
                            .createdAt(savedProfile.getCreatedAt())
                            .updatedAt(savedProfile.getUpdatedAt())
                            .build())
                    .expiresAt(expiresAt)
                    .rememberMe(false)
                    .message("Inscription réussie")
                    .loginTime(LocalDateTime.now())
                    .build();

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription: {}", e.getMessage(), e);
            throw new AuthenticationException("Erreur lors de l'inscription: " + e.getMessage());
        }
    }

    public Optional<Profile> getCurrentUser(String email) {
        return profileRepository.findByEmail(email);
    }
}