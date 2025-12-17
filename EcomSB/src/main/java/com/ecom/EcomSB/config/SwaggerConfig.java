package com.ecom.EcomSB.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        SecurityScheme bearerScheme = new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer Token");

        SecurityRequirement bearerRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .info(new Info()
                        .title("Sprint Boot E-Commerce API")
                        .version("1.0")
                        .description("This is a Sprint-Boot Project for E-Commerce")
                        .license(new License().name("Apache 2.0").url("http://iaddurlafterbuilditcompletely"))
                        .contact(new Contact().name("Rohit Sharma").email("rohitsharma45055@gmail.com").url("https://github.com/ROHITSHARMA174593"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerScheme))
                .addSecurityItem(bearerRequirement);
    }
}
