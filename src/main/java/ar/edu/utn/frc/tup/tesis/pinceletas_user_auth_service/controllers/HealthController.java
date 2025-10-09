package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.common.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para verificar el estado de salud de la aplicación.
 * Proporciona un endpoint simple para monitoreo y health checks.
 */
@RestController
public class HealthController {

    /**
     * Verifica que la aplicación esté en funcionamiento.
     * Endpoint público sin autenticación para monitoreo externo.
     *
     * @return MessageResponse indicando que la aplicación está activa.
     */
    @GetMapping("/health")
    public ResponseEntity<MessageResponse> health() {
        return ResponseEntity.ok(MessageResponse.of("Application is running!"));
    }
}
