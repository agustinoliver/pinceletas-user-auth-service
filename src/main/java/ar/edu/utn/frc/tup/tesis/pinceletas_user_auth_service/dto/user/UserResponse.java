package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private RoleEnum role;
    private boolean activo;

    private String calle;
    private String numero;
    private String ciudad;
    private String piso;
    private String barrio;
    private String pais;
    private String provincia;
    private String codigoPostal;
    private String manzana;
    private String lote;
}
