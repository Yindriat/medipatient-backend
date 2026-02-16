package com.medipatient.consultation.mapper;

import com.medipatient.appointment.mapper.AppointmentMapper;
import com.medipatient.consultation.dto.ConsultationDto;
import com.medipatient.consultation.dto.CreateConsultationDto;
import com.medipatient.consultation.dto.UpdateConsultationDto;
import com.medipatient.consultation.model.Consultation;
import com.medipatient.doctor.mapper.DoctorMapper;
import com.medipatient.patient.mapper.PatientMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        uses = {AppointmentMapper.class, PatientMapper.class, DoctorMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConsultationMapper {

    ConsultationDto toDto(Consultation consultation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "consultationDate", expression = "java(createConsultationDto.getConsultationDate() != null ? createConsultationDto.getConsultationDate() : java.time.ZonedDateTime.now())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Consultation toEntity(CreateConsultationDto createConsultationDto);

    void updateEntityFromDto(UpdateConsultationDto updateConsultationDto, @MappingTarget Consultation consultation);
}