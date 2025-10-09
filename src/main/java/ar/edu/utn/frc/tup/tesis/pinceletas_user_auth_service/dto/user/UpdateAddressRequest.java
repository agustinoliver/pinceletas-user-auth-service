package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Solicitud para actualizar la dirección completa de un usuario.
 * Contiene todos los campos opcionales de una dirección postal.
 */
@Data
public class UpdateAddressRequest {
    /** Nombre de la calle (máximo 200 caracteres). */
    @Size(max = 200, message = "La calle no puede exceder 200 caracteres")
    private String calle;

    /** Número de la dirección (máximo 20 caracteres). */
    @Size(max = 20, message = "El número no puede exceder 20 caracteres")
    private String numero;

    /** Piso o departamento (máximo 20 caracteres). */
    @Size(max = 20, message = "El piso no puede exceder 20 caracteres")
    private String piso;

    /** Barrio o vecindario (máximo 200 caracteres). */
    @Size(max = 200, message = "El barrio no puede exceder 200 caracteres")
    private String barrio;

    /** Ciudad o localidad (máximo 100 caracteres). */
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String ciudad;

    /** Provincia o estado (máximo 100 caracteres). */
    @Size(max = 100, message = "La provincia no puede exceder 100 caracteres")
    private String provincia;

    /** País (máximo 100 caracteres). */
    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    private String pais;

    /** Código postal (máximo 20 caracteres). */
    @Size(max = 20, message = "El código postal no puede exceder 20 caracteres")
    private String codigoPostal;

    /** Manzana (para urbanizaciones, máximo 20 caracteres). */
    @Size(max = 20, message = "La manzana no puede exceder 20 caracteres")
    private String manzana;

    /** Lote (para terrenos, máximo 20 caracteres). */
    @Size(max = 20, message = "El lote no puede exceder 20 caracteres")
    private String lote;
}
