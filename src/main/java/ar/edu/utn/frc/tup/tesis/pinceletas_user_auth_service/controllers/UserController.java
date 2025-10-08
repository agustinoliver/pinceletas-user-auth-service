package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.common.MessageResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.ChangePasswordRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UpdateAddressRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UpdateUserRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UserResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestión de perfiles de usuario")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile/{email}")
    @Operation(summary = "Obtener perfil de usuario", description = "Cualquier usuario autenticado puede ver perfiles")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PutMapping("/profile/{email}")
    @Operation(summary = "Actualizar perfil de usuario", description = "Cualquier usuario autenticado puede actualizar perfiles")
    public ResponseEntity<UserResponse> updateUserProfile(
            @PathVariable String email,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(email, request));
    }

    @PutMapping("/profile/{email}/address")
    @Operation(summary = "Actualizar dirección", description = "Cualquier usuario autenticado puede actualizar direcciones")
    public ResponseEntity<UserResponse> updateUserAddress(
            @PathVariable String email,
            @Valid @RequestBody UpdateAddressRequest request) {
        return ResponseEntity.ok(userService.updateUserAddress(email, request));
    }

    @PutMapping("/profile/{email}/password")
    @Operation(summary = "Cambiar contraseña", description = "Cualquier usuario autenticado puede cambiar contraseñas")
    public ResponseEntity<MessageResponse> changePassword(
            @PathVariable String email,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(email, request);
        return ResponseEntity.ok(MessageResponse.of("Contraseña cambiada exitosamente"));
    }

    @DeleteMapping("/profile/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Eliminar usuario",
            description = "SOLO ADMIN - Elimina permanentemente un usuario del sistema"
    )
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok(MessageResponse.of("Usuario eliminado exitosamente"));
    }

    @PutMapping("/profile/{email}/deactivate")
    @Operation(summary = "Desactivar usuario", description = "Cualquier usuario autenticado puede desactivar cuentas")
    public ResponseEntity<MessageResponse> deactivateUser(@PathVariable String email) {
        userService.deactivateUser(email);
        return ResponseEntity.ok(MessageResponse.of("Usuario desactivado exitosamente"));
    }
}