package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth;

import jakarta.validation.constraints.*;

import lombok.Data;

/**
 * Solicitud de registro de nuevo usuario con credenciales tradicionales.
 * Contiene todos los datos necesarios para crear un nuevo usuario.
 */
@Data
public class RegisterUserRequest {

    /** Primer nombre del usuario. */
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    /** Apellido del usuario. */
    @NotBlank(message = "El apellido es requerido")
    private String apellido;

    /** Email único del usuario. */
    @Email(message = "Debe ser un email válido")
    @NotBlank(message = "El email es requerido")
    private String email;

    /** Número de teléfono del usuario. */
    @NotBlank(message = "El teléfono es requerido")
    private String telefono;

    /** Contraseña del usuario (mínimo 6 caracteres). */
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    /** Confirmación de la contraseña para validación. */
    @NotBlank(message = "La confirmación de contraseña es requerida")
    private String confirmPassword;
}
