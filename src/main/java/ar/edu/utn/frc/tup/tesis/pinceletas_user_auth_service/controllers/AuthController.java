package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.common.MessageResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperación de contraseña", description = "Envía un token de recuperación por email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token de recuperación enviado"),
            @ApiResponse(responseCode = "400", description = "Email inválido")
    })
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(MessageResponse.of("Si el email existe en nuestro sistema, recibirás un token de recuperación"));
    }

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

    @PostMapping("/firebase/login")
    @Operation(summary = "Login con Firebase", description = "Autenticación usando Google/Facebook/etc via Firebase")
    public ResponseEntity<AuthResponse> loginWithFirebase(@Valid @RequestBody FirebaseLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithFirebase(request));
    }

    @PostMapping("/firebase/register")
    @Operation(summary = "Registro con Firebase", description = "Registro usando Google/Facebook/etc via Firebase")
    public ResponseEntity<AuthResponse> registerWithFirebase(@Valid @RequestBody FirebaseRegisterRequest request) {
        return ResponseEntity.ok(authService.registerWithFirebase(request));
    }
}
