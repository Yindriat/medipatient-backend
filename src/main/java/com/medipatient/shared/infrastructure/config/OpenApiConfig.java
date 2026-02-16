package com.medipatient.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI medipatientOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MediPatient API")
                        .description("API REST complète pour la gestion d'une clinique médicale - 200+ endpoints disponibles")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("MediPatient Team")
                                .email("contact@medipatient.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:7080")
                                .description("Serveur de développement"),
                        new Server()
                                .url("https://medipatient.com")
                                .description("Serveur de production")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtenu via /auth/login - Format: 'Bearer {token}'")));
    }
}