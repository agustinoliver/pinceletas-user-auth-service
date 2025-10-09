package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth.*;

/**
 * Servicio para gestionar operaciones de autenticación y registro de usuarios.
 * Proporciona métodos para login, registro tradicional y con Firebase, y recuperación de contraseñas.
 */
public interface AuthService {

    /**
     * Registra un nuevo usuario con credenciales tradicionales (email y contraseña).
     *
     * @param request Datos del usuario a registrar.
     * @return AuthResponse con el token JWT generado.
     */
    AuthResponse register(RegisterUserRequest request);

    /**
     * Autentica un usuario existente con credenciales tradicionales.
     *
     * @param request Credenciales de login (email y contraseña).
     * @return AuthResponse con el token JWT generado.
     */
    AuthResponse login(LoginRequest request);

    /**
     * Inicia el proceso de recuperación de contraseña para un usuario.
     * Genera un token y lo envía por email al usuario.
     *
     * @param request Solicitud con el email del usuario.
     */
    void forgotPassword(ForgotPasswordRequest request);

    /**
     * Restablece la contraseña de un usuario usando un token de recuperación válido.
     *
     * @param request Solicitud con el token y nueva contraseña.
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * Registra un nuevo usuario usando autenticación con Firebase.
     * Valida el token de Firebase y crea el usuario en el sistema.
     *
     * @param request Solicitud con token de Firebase y datos adicionales.
     * @return AuthResponse con el token JWT del sistema.
     */
    AuthResponse registerWithFirebase(FirebaseRegisterRequest request);

    /**
     * Autentica un usuario existente usando autenticación con Firebase.
     * Valida el token de Firebase y genera un JWT del sistema.
     *
     * @param request Solicitud con token de Firebase.
     * @return AuthResponse con el token JWT del sistema.
     */
    AuthResponse loginWithFirebase(FirebaseLoginRequest request);

}
