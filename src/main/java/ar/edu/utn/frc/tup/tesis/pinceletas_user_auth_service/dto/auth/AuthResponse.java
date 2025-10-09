package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de autenticación que contiene el token JWT generado.
 * Se utiliza como respuesta para login y registro exitosos.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    /** Token JWT generado para la autenticación del usuario. */
    private String token;
}
