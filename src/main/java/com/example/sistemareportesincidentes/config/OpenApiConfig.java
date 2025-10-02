package com.example.sistemareportesincidentes.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.openapi.dev-url:htpp://localhost:8080}")
    private String devUrl;

    @Value("${app.openapi.prod-url:htpp://localhost:8080}")
    private String prodUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        // Configurar servidor de desarrollo
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Servidor de Desarrollo");

        // Configurar servidor de producción
        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Servidor de Producción");

        // Información de contacto
        Contact contact = new Contact();
        contact.setEmail("soporte@empresa.com");
        contact.setName("Equipo de Desarrollo");
        contact.setUrl("https://www.empresa.com");

        // Licencia
        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        // Información general de la API
        Info info = new Info()
                .title("Sistema de Reportes de Incidentes API")
                .version("1.0")
                .contact(contact)
                .description("API REST para la gestión de incidentes, técnicos, clientes y servicios. " +
                        "Esta API permite la administración completa del sistema de reportes de incidentes " +
                        "con autenticación JWT y control de acceso basado en roles.")
                .termsOfService("https://www.empresa.com/terms")
                .license(mitLicense);

        // Configuración de seguridad JWT
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese el token JWT obtenido del endpoint de login")
                        )
                );
    }
}
