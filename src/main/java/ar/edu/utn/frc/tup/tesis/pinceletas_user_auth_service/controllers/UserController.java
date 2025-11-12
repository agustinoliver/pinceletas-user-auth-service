package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.common.MessageResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.ChangePasswordRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UpdateAddressRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UpdateUserRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UserResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para gestión de perfiles de usuario.
 * Proporciona operaciones CRUD sobre usuarios autenticados y sus datos personales.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestión de perfiles de usuario")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    /**
     * Servicio para gestión de usuarios y sus datos.
     */
    private final UserService userService;

    /**
     * Obtiene el perfil completo de un usuario por su email.
     * Cualquier usuario autenticado puede consultar perfiles.
     *
     * @param email Email del usuario a consultar.
     * @return UserResponse con los datos del perfil del usuario.
     */
    @GetMapping("/profile/{email}")
    @Operation(summary = "Obtener perfil de usuario", description = "Cualquier usuario autenticado puede ver perfiles")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Actualiza los datos básicos del perfil de un usuario.
     * Permite modificar nombre, apellido, email y teléfono.
     *
     * @param email   Email del usuario a actualizar.
     * @param request Nuevos datos del usuario.
     * @return UserResponse con los datos actualizados del usuario.
     */
    @PutMapping("/profile/{email}")
    @Operation(summary = "Actualizar perfil de usuario", description = "Cualquier usuario autenticado puede actualizar perfiles")
    public ResponseEntity<UserResponse> updateUserProfile(
            @PathVariable String email,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(email, request));
    }

    /**
     * Actualiza la dirección completa de un usuario.
     * Incluye calle, número, ciudad, provincia, país y código postal.
     *
     * @param email   Email del usuario.
     * @param request Datos de la nueva dirección.
     * @return UserResponse con los datos actualizados incluyendo la dirección.
     */
    @PutMapping("/profile/{email}/address")
    @Operation(summary = "Actualizar dirección", description = "Cualquier usuario autenticado puede actualizar direcciones")
    public ResponseEntity<UserResponse> updateUserAddress(
            @PathVariable String email,
            @Valid @RequestBody UpdateAddressRequest request) {
        return ResponseEntity.ok(userService.updateUserAddress(email, request));
    }

    /**
     * Cambia la contraseña de un usuario.
     * Requiere la contraseña actual para validar la operación.
     *
     * @param email   Email del usuario.
     * @param request Contraseña actual y nueva contraseña.
     * @return MessageResponse confirmando el cambio de contraseña.
     */
    @PutMapping("/profile/{email}/password")
    @Operation(summary = "Cambiar contraseña", description = "Cualquier usuario autenticado puede cambiar contraseñas")
    public ResponseEntity<MessageResponse> changePassword(
            @PathVariable String email,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(email, request);
        return ResponseEntity.ok(MessageResponse.of("Contraseña cambiada exitosamente"));
    }

    /**
     * Elimina permanentemente un usuario del sistema.
     * Solo los usuarios con rol ADMIN pueden ejecutar esta operación.
     *
     * @param email Email del usuario a eliminar.
     * @return MessageResponse confirmando la eliminación.
     */
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

    /**
     * Desactiva temporalmente una cuenta de usuario.
     * El usuario no podrá iniciar sesión hasta que se reactive.
     *
     * @param email Email del usuario a desactivar.
     * @return MessageResponse confirmando la desactivación.
     */
    @PutMapping("/profile/{email}/deactivate")
    @Operation(summary = "Desactivar usuario", description = "Cualquier usuario autenticado puede desactivar cuentas")
    public ResponseEntity<MessageResponse> deactivateUser(@PathVariable String email) {
        userService.deactivateUser(email);
        return ResponseEntity.ok(MessageResponse.of("Usuario desactivado exitosamente"));
    }



    /**
     * Obtiene información básica del usuario por email para comunicación entre servicios.
     * Este endpoint está diseñado específicamente para comunicación entre microservicios.
     *
     * @param email Email del usuario a consultar.
     * @return UserBasicInfo con los datos básicos del usuario.
     */
    @GetMapping("/by-email")
    @Operation(summary = "Obtener información básica de usuario por email",
            description = "Endpoint para comunicación entre microservicios")
    public ResponseEntity<UserBasicInfo> getUserByEmail(@RequestParam String email) {
        try {
            UserEntity user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            UserBasicInfo response = new UserBasicInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getNombre(),
                    user.getApellido(),
                    user.getRole().name()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Record para la respuesta específica de este endpoint
    public record UserBasicInfo(Long id, String email, String nombre, String apellido, String role) {}


    /**
     * Obtiene el email de un usuario por su ID
     * Endpoint para comunicación entre microservicios
     */
    @GetMapping("/by-id/{id}")
    @Operation(summary = "Obtener email por ID de usuario",
            description = "Endpoint para comunicación entre microservicios")
    public ResponseEntity<UserEmailInfo> getUserEmailById(@PathVariable Long id) {
        try {
            UserEntity user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            UserEmailInfo response = new UserEmailInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getNombre(),
                    user.getApellido()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Record para la respuesta
    public record UserEmailInfo(Long id, String email, String nombre, String apellido) {}

    /**
     * Marca los términos y condiciones como aceptados por el usuario.
     *
     * @param userId ID del usuario que acepta los términos.
     * @return MessageResponse confirmando la operación.
     */
    @PutMapping("/profile/{userId}/aceptar-terminos")
    @Operation(summary = "Aceptar términos y condiciones", description = "Marca los términos como aceptados por el usuario")
    public ResponseEntity<MessageResponse> aceptarTerminos(@PathVariable Long userId) {
        try {
            userService.marcarTerminosAceptados(userId);
            return ResponseEntity.ok(MessageResponse.of("Términos y condiciones aceptados exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.of("Error al aceptar términos y condiciones: " + e.getMessage()));
        }
    }

    /**
     * Verifica si el usuario ya aceptó los términos y condiciones.
     *
     * @param userId ID del usuario a verificar.
     * @return ResponseEntity con el estado de aceptación.
     */
    @GetMapping("/profile/{userId}/terminos-aceptados")
    @Operation(summary = "Verificar términos aceptados", description = "Verifica si el usuario aceptó los términos")
    public ResponseEntity<Map<String, Boolean>> verificarTerminosAceptados(@PathVariable Long userId) {
        try {
            boolean aceptados = userService.verificarTerminosAceptados(userId);
            Map<String, Boolean> response = new HashMap<>();
            response.put("terminosAceptados", aceptados);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}