package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Solicitud de login con credenciales tradicionales (email y contraseña).
 * Valida las credenciales del usuario para autenticación.
 */
@Data
public class LoginRequest {

    /** Email del usuario registrado. */
    @Email
    @NotBlank
    private String email;

    /** Contraseña del usuario. */
    @NotBlank
    private String password;
}
