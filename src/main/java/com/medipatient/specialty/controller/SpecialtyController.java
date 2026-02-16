package com.medipatient.specialty.controller;

import com.medipatient.specialty.dto.CreateSpecialtyDto;
import com.medipatient.specialty.dto.SpecialtyDto;
import com.medipatient.specialty.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
@Tag(name = "Spécialités", description = "Gestion des spécialités médicales")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping
    public ResponseEntity<Page<SpecialtyDto>> getAllSpecialties(
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            @RequestParam(required = false) String search) {
        
        Page<SpecialtyDto> specialties = search != null 
            ? specialtyService.searchSpecialties(search, pageable)
            : specialtyService.getAllSpecialties(pageable);
            
        return ResponseEntity.ok(specialties);
    }

    @GetMapping("/list")
    public ResponseEntity<List<SpecialtyDto>> getAllSpecialtiesAsList() {
        List<SpecialtyDto> specialties = specialtyService.getAllSpecialtiesAsList();
        return ResponseEntity.ok(specialties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyDto> getSpecialtyById(@PathVariable UUID id) {
        return specialtyService.getSpecialtyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SpecialtyDto> getSpecialtyByName(@PathVariable String name) {
        return specialtyService.getSpecialtyByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SpecialtyDto> createSpecialty(@Valid @RequestBody CreateSpecialtyDto createSpecialtyDto) {
        try {
            SpecialtyDto createdSpecialty = specialtyService.createSpecialty(createSpecialtyDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSpecialty);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyDto> updateSpecialty(@PathVariable UUID id, 
                                                       @Valid @RequestBody CreateSpecialtyDto updateSpecialtyDto) {
        try {
            SpecialtyDto updatedSpecialty = specialtyService.updateSpecialty(id, updateSpecialtyDto);
            return ResponseEntity.ok(updatedSpecialty);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable UUID id) {
        try {
            specialtyService.deleteSpecialty(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}