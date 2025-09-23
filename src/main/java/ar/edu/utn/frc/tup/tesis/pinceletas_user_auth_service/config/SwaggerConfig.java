package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pinceletas User Auth Service API")
                        .version("1.0")
                        .description("API para gestión de autenticación y usuarios de Pinceletas")
                        .contact(new Contact()
                                .name("Equipo Pinceletas")
                                .email("support@pinceletas.com")));
    }
}
