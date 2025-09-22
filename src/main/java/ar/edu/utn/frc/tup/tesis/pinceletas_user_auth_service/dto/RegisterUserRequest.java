package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String telefono;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String confirmPassword;
}
