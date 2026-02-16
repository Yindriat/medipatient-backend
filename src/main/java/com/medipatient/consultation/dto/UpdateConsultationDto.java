package com.medipatient.consultation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConsultationDto {
    
    private ZonedDateTime consultationDate;
    private String symptoms;
    private String diagnosis;
    private String treatmentPlan;
    private Map<String, Object> vitals;
}