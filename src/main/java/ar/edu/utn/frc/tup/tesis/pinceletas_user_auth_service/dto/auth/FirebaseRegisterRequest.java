package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Solicitud de registro con Firebase Authentication.
 * Contiene el token de Firebase y datos adicionales del usuario.
 */
@Data
public class FirebaseRegisterRequest {
    /** Token ID de Firebase proporcionado por el proveedor de autenticación. */
    @NotBlank(message = "El token de Firebase es requerido")
    private String firebaseIdToken;

    /** Primer nombre del usuario (opcional). */
    private String firstName;

    /** Apellido del usuario (opcional). */
    private String lastName;

    /** Número de teléfono del usuario (opcional). */
    private String phoneNumber;
}
