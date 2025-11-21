package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * Configuración de CORS (Cross-Origin Resource Sharing) para la aplicación.
 * Permite que clientes frontend desde diferentes orígenes accedan a la API.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer{

    /** Orígenes permitidos para CORS, separados por comas. */
    @Value("${app.cors.allowed-origins:https://pinceletas-frontend.onrender.com}")
    private String allowedOrigins;

    /**
     * Configura las reglas de CORS para todos los endpoints de la aplicación.
     * Permite peticiones desde los orígenes configurados con credenciales.
     *
     * @param registry Registro de configuración CORS.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOrigins.split(","))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
