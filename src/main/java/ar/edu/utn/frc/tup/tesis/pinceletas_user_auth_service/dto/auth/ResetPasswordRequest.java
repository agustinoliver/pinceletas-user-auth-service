package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Solicitud para restablecer la contraseña usando el token de recuperación.
 * Valida el token y establece la nueva contraseña.
 */
@Data
public class ResetPasswordRequest {

    /** Token de recuperación de 6 dígitos enviado por email. */
    @NotBlank(message = "El token es requerido")
    private String token;

    /** Nueva contraseña del usuario (mínimo 6 caracteres). */
    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String newPassword;

    /** Confirmación de la nueva contraseña para validación. */
    @NotBlank(message = "La confirmación de la contraseña es requerida")
    private String confirmNewPassword;
}
