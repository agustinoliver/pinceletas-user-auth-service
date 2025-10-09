package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Solicitud para cambiar la contraseña de un usuario.
 * Requiere validación de la contraseña actual y confirmación de la nueva.
 */
@Data
public class ChangePasswordRequest {
    /** Contraseña actual del usuario para validar la operación. */
    @NotBlank(message = "La contraseña actual es requerida")
    private String currentPassword;

    /** Nueva contraseña del usuario (mínimo 6 caracteres). */
    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String newPassword;

    /** Confirmación de la nueva contraseña para validación. */
    @NotBlank(message = "La confirmación de la contraseña es requerida")
    private String confirmNewPassword;
}
