package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    @PostConstruct
    public void init() {
        log.info("🔍 === CONFIGURACIÓN DE EMAIL DEBUG ===");
        log.info("🔍 Email username: {}", emailUsername);
        log.info("🔍 From email: {}", fromEmail);
        log.info("🔍 === FIN DEBUG ===");
    }

    public void sendPasswordResetEmail(String to, String token) {
        log.info("🔍 DEBUG - Antes de enviar email:");
        log.info("🔍 Username: {}", emailUsername);
        log.info("🔍 From: {}", fromEmail);
        log.info("🔍 To: {}", to);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("🔒 Recuperación de contraseña - Pinceletas");

            String htmlContent = buildPasswordResetEmailContent(token);
            helper.setText(htmlContent, true);

            log.info("Intentando enviar email a: {}", to);
            mailSender.send(mimeMessage);
            log.info("Email HTML enviado exitosamente");

        } catch (MessagingException e) {
            log.error("Error enviando el email HTML de recuperación", e);
            throw new RuntimeException("Error enviando el email de recuperación: " + e.getMessage());
        }
    }
    private String buildPasswordResetEmailContent(String token) {
        return """
            <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; padding: 30px;">
                <div style="max-width: 600px; margin: auto; background-color: white; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); overflow: hidden;">
                    <div style="background-color: #ED620C; padding: 20px; text-align: center;">
                        <h1 style="color: #FFFFFF; margin: 0; font-size: 24px;">Recuperación de contraseña</h1>
                    </div>

                    <div style="padding: 30px; color: #333;">
                        <p style="font-size: 16px;">Hola,</p>
                        <p style="font-size: 16px;">Recibimos una solicitud para restablecer tu contraseña en <strong>Pinceletas</strong>.</p>

                        <div style="background-color: #EBED6D; padding: 15px; text-align: center; border-radius: 8px; margin: 25px 0;">
                            <p style="margin: 0; font-size: 20px; font-weight: bold; color: #ED620C;">%s</p>
                        </div>

                        <p style="font-size: 14px; color: #555;">Este código expira en <strong>15 minutos</strong>. Por razones de seguridad, no compartas este token con nadie.</p>

                        <p style="font-size: 14px; color: #555;">Si no solicitaste este cambio, podés ignorar este correo.</p>

                        <p style="margin-top: 30px; font-size: 14px; color: #333;">Saludos,<br><strong>Equipo Pinceletas 🎨</strong></p>
                    </div>

                    <div style="background-color: #f3f3f3; text-align: center; padding: 15px; font-size: 12px; color: #777;">
                        © 2025 Pinceletas. Todos los derechos reservados.
                    </div>
                </div>
            </div>
        """.formatted(token);
    }
}
