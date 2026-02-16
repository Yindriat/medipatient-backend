package com.medipatient.specialty.mapper;

import com.medipatient.specialty.dto.CreateSpecialtyDto;
import com.medipatient.specialty.dto.SpecialtyDto;
import com.medipatient.specialty.model.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SpecialtyMapper {

    SpecialtyDto toDto(Specialty specialty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Specialty toEntity(CreateSpecialtyDto createSpecialtyDto);

    void updateEntityFromDto(CreateSpecialtyDto updateSpecialtyDto, @MappingTarget Specialty specialty);
}