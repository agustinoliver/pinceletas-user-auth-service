package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FirebaseRegisterRequest {
    @NotBlank(message = "El token de Firebase es requerido")
    private String firebaseIdToken;

    private String firstName;
    private String lastName;
    private String phoneNumber;
}
