package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI para documentación de la API.
 * Define la información general de la API y el esquema de seguridad JWT.
 */
@Configuration
public class SwaggerConfig {

    /** Nombre del esquema de seguridad utilizado en la documentación. */
    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    /**
     * Configura la documentación OpenAPI personalizada de la aplicación.
     * Incluye información del servicio y configuración de autenticación JWT.
     *
     * @return OpenAPI configurada con información y seguridad.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pinceletas User Auth Service API")
                        .version("1.0")
                        .description("API para gestión de autenticación y usuarios de Pinceletas")
                        .contact(new Contact()
                                .name("Equipo Pinceletas")
                                .email("support@pinceletas.com")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa el token JWT obtenido del login (sin 'Bearer ')")
                        )
                );
    }
}