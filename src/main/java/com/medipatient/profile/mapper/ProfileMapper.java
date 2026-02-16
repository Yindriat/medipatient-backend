package com.medipatient.profile.mapper;

import com.medipatient.profile.dto.CreateProfileDto;
import com.medipatient.profile.dto.ProfileDto;
import com.medipatient.profile.dto.UpdateProfileDto;
import com.medipatient.profile.model.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileMapper {

    ProfileDto toDto(Profile profile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Profile toEntity(CreateProfileDto createProfileDto);

    void updateEntityFromDto(UpdateProfileDto updateProfileDto, @MappingTarget Profile profile);
}