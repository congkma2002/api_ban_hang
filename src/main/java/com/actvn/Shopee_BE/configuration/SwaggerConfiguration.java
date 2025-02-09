package com.actvn.Shopee_BE.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customOpenApi() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Shopee clone application API Documentation")
                        .description("API Documentation with Swagger")
                        .version("v1.0")
                )
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Bearer")
                                .bearerFormat("JWT"))
                )
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
        return openAPI;
    }
}
