package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health")
    public String health() {
        return "✅ Application is running!";
    }
}
