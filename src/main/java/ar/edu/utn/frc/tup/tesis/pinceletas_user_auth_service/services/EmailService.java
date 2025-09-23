package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@pinceletas.com}")
    private String fromEmail;

    @Value("${spring.mail.username}")
    private String emailUsername;

    public void sendPasswordResetEmail(String to, String token) {
        log.info("Email username configurado: {}", emailUsername != null ? "✓ Configurado" : "✗ No configurado");
        log.info("From email configurado: {}", fromEmail);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Recuperación de contraseña - Pinceletas");
        message.setText(buildPasswordResetEmailContent(token));

        try {
            log.info("Intentando enviar email a: {}", to);
            mailSender.send(message);
            log.info("Email enviado exitosamente");
        } catch (Exception e) {
            log.error("Error enviando el email de recuperación", e);
            throw new RuntimeException("Error enviando el email de recuperación: " + e.getMessage());
        }
    }

    private String buildPasswordResetEmailContent(String token) {
        return """
            Hola,
            
            Recibimos una solicitud para restablecer tu contraseña en Pinceletas.
            
            Tu token de recuperación es: %s
            
            Este token expira en 15 minutos.
            
            Si no solicitaste este cambio, puedes ignorar este email.
            
            Saludos,
            Equipo Pinceletas
            """.formatted(token);
    }
}
