package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.config;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {
    private final PasswordResetService passwordResetService;
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredTokens() {
        log.info("Ejecutando limpieza de tokens expirados");
        passwordResetService.cleanupExpiredTokens();
        log.info("Limpieza de tokens expirados completada");
    }
}
