package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Solicitud para actualizar los datos básicos del perfil de usuario.
 * Permite modificar información personal como nombre, apellido, email y teléfono.
 */
@Data
public class UpdateUserRequest {
    /** Nuevo nombre del usuario. */
    @NotBlank
    private String nombre;

    /** Nuevo apellido del usuario. */
    @NotBlank
    private String apellido;

    /** Nuevo email del usuario (debe ser único en el sistema). */
    @Email
    @NotBlank
    private String email;

    /** Nuevo número de teléfono del usuario. */
    @NotBlank
    private String telefono;
}
