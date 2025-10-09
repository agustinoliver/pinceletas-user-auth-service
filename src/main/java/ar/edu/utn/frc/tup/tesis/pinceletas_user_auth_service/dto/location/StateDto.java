package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un estado/provincia dentro de un país.
 * Utilizado para formularios de dirección y selección de ubicaciones.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateDto {
    /** Código único del estado/provincia dentro del país. */
    private String code;
    /** Nombre completo del estado/provincia. */
    private String name;
    /** Código del país al que pertenece este estado. */
    private String countryCode;
    /** Tipo de división administrativa (state, province, department, etc.). */
    private String type;
}
