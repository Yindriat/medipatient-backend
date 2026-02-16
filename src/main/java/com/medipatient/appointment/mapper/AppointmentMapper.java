package com.medipatient.appointment.mapper;

import com.medipatient.appointment.dto.AppointmentDto;
import com.medipatient.appointment.dto.CreateAppointmentDto;
import com.medipatient.appointment.dto.UpdateAppointmentDto;
import com.medipatient.appointment.model.Appointment;
import com.medipatient.doctor.mapper.DoctorMapper;
import com.medipatient.patient.mapper.PatientMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        uses = {PatientMapper.class, DoctorMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AppointmentMapper {

    AppointmentDto toDto(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "paymentStatus", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Appointment toEntity(CreateAppointmentDto createAppointmentDto);

    void updateEntityFromDto(UpdateAppointmentDto updateAppointmentDto, @MappingTarget Appointment appointment);
}