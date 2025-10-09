package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Solicitud de login con Firebase Authentication.
 * Contiene el token ID de Firebase obtenido del cliente.
 */
@Data
public class FirebaseLoginRequest {
    /** Token ID de Firebase proporcionado por el proveedor de autenticación (Google, Facebook, etc.). */
    @NotBlank(message = "El token de Firebase es requerido")
    private String firebaseIdToken;
}
