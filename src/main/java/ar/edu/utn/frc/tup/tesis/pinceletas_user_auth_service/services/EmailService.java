package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio para envío de emails del sistema usando Resend.
 * Gestiona el envío de emails HTML con templates personalizados para diferentes propósitos.
 * Actualmente implementa el envío de emails de recuperación de contraseña.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    /** Cliente de Resend para envío de emails. */
    private final Resend resendClient;

    /** Email remitente configurado para todos los emails del sistema. */
    @Value("${app.mail.from:noreply@pinceletas.com}")
    private String fromEmail;

    /** Nombre del remitente (opcional). */
    @Value("${app.mail.from-name:Pinceletas}")
    private String fromName;

    /**
     * Método de inicialización que logra la configuración de email para debugging.
     * Se ejecuta automáticamente después de la construcción del bean.
     */
    @PostConstruct
    public void init() {
        log.info("🔍 === CONFIGURACIÓN DE RESEND EMAIL ===");
        log.info("🔍 From email: {}", fromEmail);
        log.info("🔍 From name: {}", fromName);
        log.info("🔍 Resend client initialized: {}", resendClient != null);
        log.info("🔍 === FIN DEBUG ===");
    }

    /**
     * Envía un email de recuperación de contraseña con el token de 6 dígitos.
     * Utiliza un template HTML personalizado con la identidad visual de Pinceletas.
     *
     * @param to Email del destinatario.
     * @param token Token de 6 dígitos para recuperación de contraseña.
     * @throws RuntimeException si hay errores en el envío del email.
     */
    public void sendPasswordResetEmail(String to, String token) {
        log.info("🔍 DEBUG - Antes de enviar email:");
        log.info("🔍 From: {} <{}>", fromName, fromEmail);
        log.info("🔍 To: {}", to);
        log.info("🔍 Token: {}", token);

        try {
            String htmlContent = buildPasswordResetEmailContent(token);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromName + " <" + fromEmail + ">")
                    .to(to)
                    .subject("🔒 Recuperación de contraseña - Pinceletas")
                    .html(htmlContent)
                    .build();

            log.info("Intentando enviar email a: {}", to);
            CreateEmailResponse response = resendClient.emails().send(params);

            log.info("✅ Email enviado exitosamente. ID: {}", response.getId());

        } catch (ResendException e) {
            log.error("❌ Error enviando el email de recuperación con Resend", e);
            log.error("Message: {}", e.getMessage());
            throw new RuntimeException("Error enviando el email de recuperación: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error inesperado enviando el email", e);
            throw new RuntimeException("Error inesperado enviando el email: " + e.getMessage());
        }
    }

    /**
     * Construye el contenido HTML del email de recuperación de contraseña.
     * Genera un template responsive con la identidad visual de la aplicación.
     *
     * @param token Token de 6 dígitos a incluir en el email.
     * @return String con el contenido HTML completo del email.
     */
    private String buildPasswordResetEmailContent(String token) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Recuperación de contraseña</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
                <div style="background-color: #f8f9fa; padding: 30px;">
                    <div style="max-width: 600px; margin: auto; background-color: white; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); overflow: hidden;">
                        <div style="background-color: #ED620C; padding: 20px; text-align: center;">
                            <h1 style="color: #FFFFFF; margin: 0; font-size: 24px;">Recuperación de contraseña</h1>
                        </div>

                        <div style="padding: 30px; color: #333;">
                            <p style="font-size: 16px;">Hola,</p>
                            <p style="font-size: 16px;">Recibimos una solicitud para restablecer tu contraseña en <strong>Pinceletas</strong>.</p>

                            <div style="background-color: #EBED6D; padding: 15px; text-align: center; border-radius: 8px; margin: 25px 0;">
                                <p style="margin: 0; font-size: 14px; color: #666; margin-bottom: 8px;">Tu código de verificación es:</p>
                                <p style="margin: 0; font-size: 32px; font-weight: bold; color: #ED620C; letter-spacing: 8px; font-family: 'Courier New', monospace;">%s</p>
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
            </body>
            </html>
        """.formatted(token);
    }
}