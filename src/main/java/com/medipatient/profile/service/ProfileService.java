package com.medipatient.profile.service;

import com.medipatient.profile.dto.CreateProfileDto;
import com.medipatient.profile.dto.ProfileDto;
import com.medipatient.profile.dto.ProfileStatsDto;
import com.medipatient.profile.dto.UpdateProfileDto;
import com.medipatient.profile.mapper.ProfileMapper;
import com.medipatient.profile.model.Profile;
import com.medipatient.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<ProfileDto> getAllProfiles(Pageable pageable) {
        return profileRepository.findAll(pageable)
                .map(profileMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ProfileDto> getProfilesByRole(Profile.Role role, Pageable pageable) {
        return profileRepository.findByRole(role, pageable)
                .map(profileMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ProfileDto> searchProfiles(String search, Profile.Role role, Boolean enabled, Pageable pageable) {
        return profileRepository.findWithFilters(role, enabled, search, pageable)
                .map(profileMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ProfileDto> getProfileById(UUID id) {
        return profileRepository.findById(id)
                .map(profileMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ProfileDto> getProfileByEmail(String email) {
        return profileRepository.findByEmail(email)
                .map(profileMapper::toDto);
    }

    public ProfileDto createProfile(CreateProfileDto createProfileDto) {
        if (profileRepository.existsByEmail(createProfileDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + createProfileDto.getEmail());
        }

        Profile profile = profileMapper.toEntity(createProfileDto);
        profile.setPassword(passwordEncoder.encode(createProfileDto.getPassword()));
        
        Profile savedProfile = profileRepository.save(profile);
        log.info("Created new profile with id: {}", savedProfile.getId());
        
        return profileMapper.toDto(savedProfile);
    }

    public ProfileDto updateProfile(UUID id, UpdateProfileDto updateProfileDto) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with id: " + id));

        if (updateProfileDto.getEmail() != null && 
            !updateProfileDto.getEmail().equals(profile.getEmail()) &&
            profileRepository.existsByEmail(updateProfileDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + updateProfileDto.getEmail());
        }

        profileMapper.updateEntityFromDto(updateProfileDto, profile);
        Profile savedProfile = profileRepository.save(profile);
        
        log.info("Updated profile with id: {}", savedProfile.getId());
        
        return profileMapper.toDto(savedProfile);
    }

    public void deleteProfile(UUID id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with id: " + id));

        profileRepository.delete(profile);
        log.info("Deleted profile with id: {}", id);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return profileRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public long countByRole(Profile.Role role) {
        return profileRepository.countByRole(role);
    }

    @Transactional(readOnly = true)
    public long countActiveProfiles() {
        return profileRepository.countActiveProfiles();
    }

    @Transactional(readOnly = true)
    public ProfileStatsDto getProfileStats() {
        return ProfileStatsDto.builder()
                .totalProfiles(countActiveProfiles())
                .patients(countByRole(Profile.Role.PATIENT))
                .doctors(countByRole(Profile.Role.DOCTOR))
                .admins(countByRole(Profile.Role.ADMIN))
                .agents(countByRole(Profile.Role.AGENT))
                .build();
    }
}