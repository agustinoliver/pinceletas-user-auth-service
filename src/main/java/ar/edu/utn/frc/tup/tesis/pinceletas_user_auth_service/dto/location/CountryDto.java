package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO para representar un país en el sistema.
 * Contiene información básica para formularios y selección de ubicaciones.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDto {
    /** Código ISO alpha-2 del país (ej: AR, US, BR). */
    private String code;
    /** Nombre común del país en español/inglés. */
    private String name;
    /** Emoji de la bandera del país. */
    private String flag;
    /** Continente al que pertenece el país. */
    private String continent;
    /** Código de la moneda principal del país. */
    private String currency;
    /** Código de marcación internacional del país. */
    private String phoneCode;
}
