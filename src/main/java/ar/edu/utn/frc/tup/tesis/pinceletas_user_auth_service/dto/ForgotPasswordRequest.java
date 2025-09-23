package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @Email(message = "Debe ser un email válido")
    @NotBlank(message = "El email es requerido")
    private String email;
}
