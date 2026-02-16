package com.medipatient.shared.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Santé", description = "Endpoints de monitoring et santé de l'application")
@RequestMapping("/health")
@Tag(name = "Health", description = "API de vérification de l'état de santé")
public class HealthController {

    @GetMapping
    @Operation(summary = "Vérifier l'état de santé de l'application")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "medipatient-backend");
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }
}