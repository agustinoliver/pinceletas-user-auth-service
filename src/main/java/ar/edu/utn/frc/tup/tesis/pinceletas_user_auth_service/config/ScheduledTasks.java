package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.config;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Componente que ejecuta tareas programadas del sistema.
 * Gestiona la limpieza automática de tokens de recuperación de contraseña expirados.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    /** Servicio para gestión de tokens de recuperación de contraseña. */
    private final PasswordResetService passwordResetService;

    /**
     * Limpia tokens de recuperación de contraseña que hayan expirado.
     * Se ejecuta cada hora (3600000 milisegundos) de forma automática.
     */
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredTokens() {
        log.info("Ejecutando limpieza de tokens expirados");
        passwordResetService.cleanupExpiredTokens();
        log.info("Limpieza de tokens expirados completada");
    }
}
