package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.config;

import com.resend.Resend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Resend para envío de emails.
 * Inicializa el cliente de Resend con la API key configurada.
 */
@Configuration
public class ResendConfig {
    /** API Key de Resend obtenida desde el dashboard. */
    @Value("${app.resend.api-key}")
    private String resendApiKey;

    /**
     * Crea el cliente de Resend configurado con la API key.
     *
     * @return Cliente Resend listo para enviar emails.
     */
    @Bean
    public Resend resendClient() {
        return new Resend(resendApiKey);
    }
}
