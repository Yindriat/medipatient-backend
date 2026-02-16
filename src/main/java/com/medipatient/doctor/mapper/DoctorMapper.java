package com.medipatient.doctor.mapper;

import com.medipatient.doctor.dto.CreateDoctorDto;
import com.medipatient.doctor.dto.DoctorDto;
import com.medipatient.doctor.dto.UpdateDoctorDto;
import com.medipatient.doctor.model.Doctor;
import com.medipatient.profile.mapper.ProfileMapper;
import com.medipatient.specialty.mapper.SpecialtyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        uses = {ProfileMapper.class, SpecialtyMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DoctorMapper {

    DoctorDto toDto(Doctor doctor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "specialty", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Doctor toEntity(CreateDoctorDto createDoctorDto);

    @Mapping(target = "specialty", ignore = true)
    void updateEntityFromDto(UpdateDoctorDto updateDoctorDto, @MappingTarget Doctor doctor);
}