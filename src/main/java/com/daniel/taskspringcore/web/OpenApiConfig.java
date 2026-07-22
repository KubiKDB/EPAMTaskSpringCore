package com.daniel.taskspringcore.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gymCrmOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym CRM REST API")
                        .description("REST API for the Gym CRM (trainees, trainers, trainings). Secured endpoints require the X-Auth-Username and X-Auth-Password headers.")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes("authUsername", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(ApiConstants.AUTH_USERNAME_HEADER))
                        .addSecuritySchemes("authPassword", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(ApiConstants.AUTH_PASSWORD_HEADER)));
    }
}
