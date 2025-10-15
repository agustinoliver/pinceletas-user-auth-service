package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.Rabbit;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.Rabbit.events.NotificacionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificacionEventService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionEventService.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange:notificaciones.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key:notificaciones.key}")
    private String routingKey;

    public NotificacionEventService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarNotificacionInicioSesion(String email, Long usuarioId, String nombreUsuario) {
        log.info("🔔 Preparando notificacion de INICIO SESIÓN para USUARIO: {}", email);

        NotificacionEvent evento = new NotificacionEvent(
                "¡Bienvenido! 👋",
                "Hola " + nombreUsuario + ", has iniciado sesión exitosamente en Pinceletas. ¡Que tengas un gran día!",
                "INICIO_SESION",
                usuarioId,
                "{\"email\": \"" + email + "\", \"tipo\": \"inicio_sesion\"}",
                "USER"
        );

        enviarEvento(evento);
        log.info("✅ Notificacion de INICIO SESIÓN enviada para USUARIO: {}", email);
    }

    public void enviarNotificacionInicioSesionFirebase(String email, Long usuarioId, String nombreUsuario) {
        log.info("🔔 Preparando notificacion de INICIO SESIÓN FIREBASE para USUARIO: {}", email);

        NotificacionEvent evento = new NotificacionEvent(
                "¡Bienvenido! 🔐",
                "Hola " + nombreUsuario + ", has iniciado sesión con Google en Pinceletas. ¡Que tengas un gran día!",
                "INICIO_SESION_FIREBASE",
                usuarioId,
                "{\"email\": \"" + email + "\", \"tipo\": \"inicio_sesion_firebase\"}",
                "USER"
        );

        enviarEvento(evento);
        log.info("✅ Notificacion de INICIO SESIÓN FIREBASE enviada para USUARIO: {}", email);
    }

    public void enviarNotificacionAdminInicioSesionFirebase(String email, Long usuarioId, String nombreUsuario) {
        log.info("🔔 Preparando notificacion para ADMIN - INICIO SESIÓN FIREBASE: {}", email);

        NotificacionEvent evento = new NotificacionEvent(
                "🔐 Inicio de sesión con Google",
                "El usuario " + nombreUsuario + " (" + email + ") ha iniciado sesión usando Google/Firebase.",
                "INICIO_SESION_FIREBASE_ADMIN",
                null,
                "{\"email\": \"" + email + "\", \"usuarioId\": " + usuarioId + ", \"tipo\": \"inicio_sesion_firebase_admin\"}",
                "ADMIN"
        );

        enviarEvento(evento);
        log.info("✅ Notificacion para ADMIN - INICIO SESIÓN FIREBASE enviada - usuario: {}", email);
    }

    public void enviarNotificacionNuevoRegistro(String email, Long usuarioId, String nombreUsuario) {
        log.info("🔔 Preparando notificacion de NUEVO REGISTRO para ADMIN");

        NotificacionEvent evento = new NotificacionEvent(
                "📈 Nuevo usuario registrado",
                "El usuario " + nombreUsuario + " (" + email + ") se ha registrado en la plataforma.",
                "NUEVO_REGISTRO",
                null,
                "{\"email\": \"" + email + "\", \"usuarioId\": " + usuarioId + ", \"tipo\": \"nuevo_registro\"}",
                "ADMIN"
        );

        enviarEvento(evento);
        log.info("✅ Notificacion de NUEVO REGISTRO enviada para ADMIN - usuario: {}", email);
    }

    public void enviarNotificacionBienvenidaRegistro(String email, Long usuarioId, String nombreUsuario) {
        log.info("🔔 Preparando notificacion de BIENVENIDA POR REGISTRO para USUARIO: {}", email);

        NotificacionEvent evento = new NotificacionEvent(
                "¡Bienvenido a Pinceletas! 🎨",
                "Hola " + nombreUsuario + ", ¡gracias por registrarte en nuestra plataforma! Esperamos que disfrutes de todos nuestros servicios.",
                "BIENVENIDA_REGISTRO",
                usuarioId, // Para el USUARIO que se registró
                "{\"email\": \"" + email + "\", \"tipo\": \"bienvenida_registro\"}",
                "USER" // Target: USUARIO
        );

        enviarEvento(evento);
        log.info("✅ Notificacion de BIENVENIDA POR REGISTRO enviada para USUARIO: {}", email);
    }

    private void enviarEvento(NotificacionEvent evento) {
        try {
            log.debug("📤 Enviando a exchange: {}, routing-key: {}", exchange, routingKey);
            log.debug("📦 Contenido del evento: {}", evento);

            rabbitTemplate.convertAndSend(exchange, routingKey, evento);

            log.info("✅ Evento enviado exitosamente a RabbitMQ");
        } catch (Exception e) {
            log.error("❌ Error enviando evento de notificacion: {}", e.getMessage(), e);
        }
    }
}