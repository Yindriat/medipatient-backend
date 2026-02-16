package com.medipatient.profile.dto;

import com.medipatient.profile.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Profile.Role role;
    private Boolean enabled;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}