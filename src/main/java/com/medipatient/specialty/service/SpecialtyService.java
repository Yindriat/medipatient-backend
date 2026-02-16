package com.medipatient.specialty.service;

import com.medipatient.specialty.dto.CreateSpecialtyDto;
import com.medipatient.specialty.dto.SpecialtyDto;
import com.medipatient.specialty.mapper.SpecialtyMapper;
import com.medipatient.specialty.model.Specialty;
import com.medipatient.specialty.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    @Transactional(readOnly = true)
    public Page<SpecialtyDto> getAllSpecialties(Pageable pageable) {
        return specialtyRepository.findAllOrderByName(pageable)
                .map(specialtyMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<SpecialtyDto> getAllSpecialtiesAsList() {
        return specialtyRepository.findAllOrderByName()
                .stream()
                .map(specialtyMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<SpecialtyDto> searchSpecialties(String search, Pageable pageable) {
        return specialtyRepository.searchSpecialties(search, pageable)
                .map(specialtyMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<SpecialtyDto> getSpecialtyById(UUID id) {
        return specialtyRepository.findById(id)
                .map(specialtyMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<SpecialtyDto> getSpecialtyByName(String name) {
        return specialtyRepository.findByName(name)
                .map(specialtyMapper::toDto);
    }

    public SpecialtyDto createSpecialty(CreateSpecialtyDto createSpecialtyDto) {
        if (specialtyRepository.existsByName(createSpecialtyDto.getName())) {
            throw new IllegalArgumentException("Specialty already exists: " + createSpecialtyDto.getName());
        }

        Specialty specialty = specialtyMapper.toEntity(createSpecialtyDto);
        Specialty savedSpecialty = specialtyRepository.save(specialty);
        
        log.info("Created new specialty with id: {}", savedSpecialty.getId());
        
        return specialtyMapper.toDto(savedSpecialty);
    }

    public SpecialtyDto updateSpecialty(UUID id, CreateSpecialtyDto updateSpecialtyDto) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Specialty not found with id: " + id));

        if (!updateSpecialtyDto.getName().equals(specialty.getName()) &&
            specialtyRepository.existsByName(updateSpecialtyDto.getName())) {
            throw new IllegalArgumentException("Specialty already exists: " + updateSpecialtyDto.getName());
        }

        specialtyMapper.updateEntityFromDto(updateSpecialtyDto, specialty);
        Specialty savedSpecialty = specialtyRepository.save(specialty);
        
        log.info("Updated specialty with id: {}", savedSpecialty.getId());
        
        return specialtyMapper.toDto(savedSpecialty);
    }

    public void deleteSpecialty(UUID id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Specialty not found with id: " + id));

        specialtyRepository.delete(specialty);
        log.info("Deleted specialty with id: {}", id);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return specialtyRepository.existsByName(name);
    }
}