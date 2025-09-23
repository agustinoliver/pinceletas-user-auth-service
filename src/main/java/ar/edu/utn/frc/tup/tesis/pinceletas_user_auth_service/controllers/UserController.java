package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile/{email}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PutMapping("/profile/{email}")
    public ResponseEntity<UserResponse> updateUserProfile(
            @PathVariable String email,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(email, request));
    }

    @DeleteMapping("/profile/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok("Usuario eliminado exitosamente");
    }

    @PutMapping("/profile/{email}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable String email) {
        userService.deactivateUser(email);
        return ResponseEntity.ok("Usuario desactivado exitosamente");
    }
}
