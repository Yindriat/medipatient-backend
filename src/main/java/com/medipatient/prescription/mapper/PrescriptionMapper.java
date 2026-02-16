package com.medipatient.prescription.mapper;

import com.medipatient.consultation.mapper.ConsultationMapper;
import com.medipatient.doctor.mapper.DoctorMapper;
import com.medipatient.patient.mapper.PatientMapper;
import com.medipatient.prescription.dto.CreatePrescriptionDto;
import com.medipatient.prescription.dto.PrescriptionDto;
import com.medipatient.prescription.dto.UpdatePrescriptionDto;
import com.medipatient.prescription.model.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        uses = {ConsultationMapper.class, PatientMapper.class, DoctorMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PrescriptionMapper {

    @Mapping(target = "medications", source = "medications")
    PrescriptionDto toDto(Prescription prescription);

    PrescriptionDto.MedicationDto medicationToDto(Prescription.Medication medication);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "consultation", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "prescriptionDate", expression = "java(createPrescriptionDto.getPrescriptionDate() != null ? createPrescriptionDto.getPrescriptionDate() : java.time.LocalDate.now())")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Prescription toEntity(CreatePrescriptionDto createPrescriptionDto);

    Prescription.Medication medicationToEntity(CreatePrescriptionDto.MedicationDto medicationDto);

    void updateEntityFromDto(UpdatePrescriptionDto updatePrescriptionDto, @MappingTarget Prescription prescription);

    Prescription.Medication updateMedicationFromDto(UpdatePrescriptionDto.MedicationDto medicationDto, @MappingTarget Prescription.Medication medication);
}