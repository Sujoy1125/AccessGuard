package com.accessguard.accessguard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI accessGuardOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("AccessGuard API")
                        .description("Employee Offboarding and Access Revocation Tracker — "
                                + "compliance module (DataAcknowledgment, HighRiskActivityFlag, "
                                + "EscalationLog, AuditSnapshot)")
                        .version("v1")
                        .contact(new Contact().name("AccessGuard Team")));
    }
}