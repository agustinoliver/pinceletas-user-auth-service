package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta con los datos completos del perfil de usuario.
 * Incluye información personal, datos de contacto, rol y dirección completa.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    /** Identificador único del usuario en la base de datos. */
    private Long id;
    /** Primer nombre del usuario. */
    private String nombre;
    /** Apellido del usuario. */
    private String apellido;
    /** Email único del usuario. */
    private String email;
    /** Número de teléfono del usuario. */
    private String telefono;
    /** Rol del usuario en el sistema (USER o ADMIN). */
    private RoleEnum role;
    /** Estado de activación de la cuenta del usuario. */
    private boolean activo;

    private boolean terminosAceptados;

    // Campos de dirección
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
