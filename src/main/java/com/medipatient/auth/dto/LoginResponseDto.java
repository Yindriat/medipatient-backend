package com.medipatient.auth.dto;

import com.medipatient.profile.dto.ProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    
    private String sessionId;
    private ProfileDto user;
    private LocalDateTime expiresAt;
    private boolean rememberMe;
    private String message;
    
    // Informations de session
    private String ipAddress;
    private LocalDateTime loginTime;
}