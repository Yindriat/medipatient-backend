package com.medipatient.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileStatsDto {
    private long totalProfiles;
    private long patients;
    private long doctors;
    private long admins;
    private long agents;
}