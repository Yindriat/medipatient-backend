package com.medipatient.patient.mapper;

import com.medipatient.patient.dto.CreatePatientDto;
import com.medipatient.patient.dto.PatientDto;
import com.medipatient.patient.dto.UpdatePatientDto;
import com.medipatient.patient.model.Patient;
import com.medipatient.profile.mapper.ProfileMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        uses = {ProfileMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PatientMapper {

    PatientDto toDto(Patient patient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Patient toEntity(CreatePatientDto createPatientDto);

    void updateEntityFromDto(UpdatePatientDto updatePatientDto, @MappingTarget Patient patient);
}