package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.common.MessageResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de autenticación y recuperación de contraseñas.
 * Maneja registro, login, recuperación de contraseña y autenticación con Firebase.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /** Servicio de autenticación para procesar las operaciones de login y registro. */
    private final AuthService authService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Datos del usuario a registrar.
     * @return AuthResponse con el token JWT generado.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Autentica un usuario existente en el sistema.
     *
     * @param request Credenciales del usuario (email y contraseña).
     * @return AuthResponse con el token JWT generado.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Inicia el proceso de recuperación de contraseña.
     * Envía un token de 6 dígitos al email del usuario si existe y está activo.
     *
     * @param request Email del usuario que solicita recuperar su contraseña.
     * @return MessageResponse con el resultado de la operación.
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperación de contraseña", description = "Envía un token de recuperación por email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token de recuperación enviado"),
            @ApiResponse(responseCode = "400", description = "Email inválido o no registrado"),
            @ApiResponse(responseCode = "404", description = "Email no encontrado")
    })
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            authService.forgotPassword(request);
            return ResponseEntity.ok(MessageResponse.of("Si el email existe en nuestro sistema, recibirás un token de recuperación"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no se encuentra registrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(MessageResponse.of(e.getMessage()));
            } else if (e.getMessage().contains("desactivada")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.of(e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.of(e.getMessage()));
            }
        }
    }

    /**
     * Restablece la contraseña del usuario usando el token de recuperación.
     *
     * @param request Token de recuperación y nueva contraseña.
     * @return MessageResponse confirmando el cambio de contraseña.
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña", description = "Cambia la contraseña usando el token de recuperación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido o datos incorrectos")
    })
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(MessageResponse.of("Contraseña restablecida exitosamente"));
    }

    /**
     * Autentica un usuario usando Firebase (Google, Facebook, etc.).
     * Valida el token de Firebase y genera un JWT del sistema.
     *
     * @param request Token ID de Firebase obtenido del frontend.
     * @return AuthResponse con el token JWT del sistema.
     */
    @PostMapping("/firebase/login")
    @Operation(summary = "Login con Firebase", description = "Autenticación usando Google/Facebook/etc via Firebase")
    public ResponseEntity<AuthResponse> loginWithFirebase(@Valid @RequestBody FirebaseLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithFirebase(request));
    }

    /**
     * Registra un nuevo usuario usando Firebase (Google, Facebook, etc.).
     * Valida el token de Firebase, crea el usuario y genera un JWT del sistema.
     *
     * @param request Token ID de Firebase y datos adicionales del usuario.
     * @return AuthResponse con el token JWT del sistema.
     */
    @PostMapping("/firebase/register")
    @Operation(summary = "Registro con Firebase", description = "Registro usando Google/Facebook/etc via Firebase")
    public ResponseEntity<AuthResponse> registerWithFirebase(@Valid @RequestBody FirebaseRegisterRequest request) {
        return ResponseEntity.ok(authService.registerWithFirebase(request));
    }
}
