package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAddressRequest {
    @Size(max = 200, message = "La calle no puede exceder 200 caracteres")
    private String calle;

    @Size(max = 20, message = "El número no puede exceder 20 caracteres")
    private String numero;

    @Size(max = 20, message = "El piso no puede exceder 20 caracteres")
    private String piso;

    @Size(max = 200, message = "El barrio no puede exceder 200 caracteres")
    private String barrio;

    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String ciudad;

    @Size(max = 100, message = "La provincia no puede exceder 100 caracteres")
    private String provincia;

    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    private String pais;

    @Size(max = 20, message = "El código postal no puede exceder 20 caracteres")
    private String codigoPostal;
}
