package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "El apellido es requerido")
    private String apellido;

    @Email(message = "Debe ser un email válido")
    @NotBlank(message = "El email es requerido")
    private String email;

    @NotBlank(message = "El teléfono es requerido")
    private String telefono;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "La confirmación de contraseña es requerida")
    private String confirmPassword;
}
