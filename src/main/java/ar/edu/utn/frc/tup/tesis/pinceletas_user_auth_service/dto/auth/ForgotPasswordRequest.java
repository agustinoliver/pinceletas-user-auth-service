package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Solicitud para iniciar el proceso de recuperación de contraseña.
 * Contiene el email del usuario que solicita el restablecimiento.
 */
@Data
public class ForgotPasswordRequest {

    /** Email del usuario que solicita recuperar su contraseña. */
    @Email(message = "Debe ser un email válido")
    @NotBlank(message = "El email es requerido")
    private String email;
}
