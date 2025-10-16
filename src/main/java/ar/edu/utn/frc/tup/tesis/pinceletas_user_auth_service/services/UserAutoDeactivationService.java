package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAutoDeactivationService {

    private final UserRepository userRepository;

    /**
     * Ejecuta automáticamente cada día a las 2:00 AM para desactivar usuarios inactivos
     */
    @Scheduled(cron = "0 0 2 * * ?") // Cada día a las 2:00 AM
    public void deactivateInactiveUsers() {
        log.info("🔍 Iniciando desactivación automática de usuarios inactivos...");

        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);

        // Encontrar usuarios que serán desactivados (para logging)
        List<UserEntity> usersToDeactivate = userRepository
                .findByActivoTrueAndLastActivityAtBefore(twoWeeksAgo);

        if (!usersToDeactivate.isEmpty()) {
            log.info("🚫 Desactivando {} usuarios inactivos por más de 2 semanas", usersToDeactivate.size());

            for (UserEntity user : usersToDeactivate) {
                log.debug("Desactivando usuario: {} - Última actividad: {}",
                        user.getEmail(), user.getLastActivityAt());
            }
        }

        // Ejecutar la actualización masiva
        int deactivatedCount = userRepository.deactivateInactiveUsers(twoWeeksAgo);

        log.info("✅ Desactivación automática completada. Usuarios desactivados: {}", deactivatedCount);
    }

    /**
     * Método manual para probar la desactivación
     */
    public int manualDeactivateInactiveUsers() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        return userRepository.deactivateInactiveUsers(twoWeeksAgo);
    }


}
