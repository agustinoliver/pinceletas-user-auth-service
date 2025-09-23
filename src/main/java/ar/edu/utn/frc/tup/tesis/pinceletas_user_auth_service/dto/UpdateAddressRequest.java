package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto;

import lombok.Data;

@Data
public class UpdateAddressRequest {
    private String calle;
    private String numero;
    private String ciudad;
    private String piso;
    private String barrio;
    private String pais;
    private String provincia;
    private String codigoPostal;
}
