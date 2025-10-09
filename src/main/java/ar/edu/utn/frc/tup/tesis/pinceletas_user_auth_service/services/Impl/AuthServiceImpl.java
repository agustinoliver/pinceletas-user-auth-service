package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.Impl;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.AuthService;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.PasswordResetService;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.UserService;
import ar.edu.utn.frc.tup.tesis.pinceletas.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de autenticación.
 * Gestiona el registro, login tradicional y con Firebase, y procesos de recuperación de contraseña.
 * Coordina entre UserService, JwtService y PasswordResetService para completar las operaciones.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    /** Servicio para gestión de usuarios y operaciones CRUD. */
    private final UserService userService;
    /** Servicio para generación y validación de tokens JWT. */
    private final JwtService jwtService;
    /** Codificador de contraseñas para verificación y hashing. */
    private final PasswordEncoder passwordEncoder;
    /** Servicio para gestión de tokens de recuperación de contraseña. */
    private final PasswordResetService passwordResetService;

    /**
     * Registra un nuevo usuario con credenciales tradicionales.
     * Crea el usuario en la base de datos y genera un token JWT para autenticación inmediata.
     *
     * @param request Datos del usuario a registrar.
     * @return AuthResponse con el token JWT generado para autenticación.
     * @throws RuntimeException si el email ya está registrado o las contraseñas no coinciden.
     */
    @Override
    public AuthResponse register(RegisterUserRequest request) {
        UserEntity user = userService.register(request);
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }

    /**
     * Autentica un usuario existente con credenciales tradicionales.
     * Verifica que el usuario exista, esté activo y las credenciales sean correctas.
     *
     * @param request Credenciales de login (email y contraseña).
     * @return AuthResponse con el token JWT generado.
     * @throws RuntimeException si el usuario no existe, está desactivado o las credenciales son inválidas.
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        UserEntity user = userService.findByEmail(request.getEmail());

        if (!user.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }

    /**
     * Inicia el proceso de recuperación de contraseña para un usuario.
     * Delega la operación al servicio especializado de recuperación de contraseña.
     *
     * @param request Solicitud con el email del usuario.
     * @throws RuntimeException si el email no existe o la cuenta está desactivada.
     */
    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        passwordResetService.initiatePasswordReset(request.getEmail());
    }

    /**
     * Restablece la contraseña de un usuario usando un token de recuperación válido.
     * Valida el token y establece la nueva contraseña en el sistema.
     *
     * @param request Solicitud con el token y nueva contraseña.
     * @throws RuntimeException si el token es inválido, expiró o las contraseñas no coinciden.
     */
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        passwordResetService.resetPassword(
                request.getToken(),
                request.getNewPassword(),
                request.getConfirmNewPassword()
        );
    }

    /**
     * Registra un nuevo usuario usando autenticación con Firebase.
     * Verifica el token de Firebase, extrae la información del usuario y lo registra en el sistema.
     *
     * @param request Solicitud con token de Firebase y datos adicionales opcionales.
     * @return AuthResponse con el token JWT del sistema.
     * @throws RuntimeException si el token de Firebase es inválido, el email ya está registrado,
     *         o hay errores en la comunicación con Firebase.
     */
    @Override
    public AuthResponse registerWithFirebase(FirebaseRegisterRequest request) {
        try {
            com.google.firebase.auth.FirebaseToken decoded =
                    com.google.firebase.auth.FirebaseAuth.getInstance()
                            .verifyIdToken(request.getFirebaseIdToken());

            String uid = decoded.getUid();
            String email = decoded.getEmail();
            String name = (String) decoded.getClaims().getOrDefault("name", email);

            if (userService.existsByEmail(email)) {
                throw new RuntimeException("El email ya está registrado");
            }

            UserEntity user = userService.registerWithFirebase(
                    uid, email, name, "firebase",
                    request.getFirstName(), request.getLastName(), request.getPhoneNumber()
            );

            String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
            return new AuthResponse(token);
        } catch (Exception e) {
            throw new RuntimeException("Error en el registro con Firebase: " + e.getMessage());
        }
    }

    /**
     * Autentica un usuario existente usando autenticación con Firebase.
     * Verifica el token de Firebase y genera un JWT del sistema para el usuario.
     * Si el usuario no existe, lo crea automáticamente (upsert operation).
     *
     * @param request Solicitud con token de Firebase.
     * @return AuthResponse con el token JWT del sistema.
     * @throws RuntimeException si el token de Firebase es inválido, la cuenta está desactivada,
     *         o hay errores en la comunicación con Firebase.
     */
    @Override
    public AuthResponse loginWithFirebase(FirebaseLoginRequest request) {
        try {
            com.google.firebase.auth.FirebaseToken decoded =
                    com.google.firebase.auth.FirebaseAuth.getInstance()
                            .verifyIdToken(request.getFirebaseIdToken());

            String uid = decoded.getUid();
            String email = decoded.getEmail();
            String name = (String) decoded.getClaims().getOrDefault("name", email);

            UserEntity user = userService.upsertFirebaseUser(uid, email, name, "firebase");

            if (!user.isActivo()) {
                throw new RuntimeException("La cuenta está desactivada");
            }

            String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
            return new AuthResponse(token);
        } catch (Exception e) {
            throw new RuntimeException("Error en el login con Firebase: " + e.getMessage());
        }
    }
}
