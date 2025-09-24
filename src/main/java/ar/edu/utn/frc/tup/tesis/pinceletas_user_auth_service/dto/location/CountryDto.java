package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDto {
    private String code;        // Código ISO (ej: "AR", "BR", "US")
    private String name;        // Nombre del país (ej: "Argentina", "Brazil", "United States")
    private String flag;        // URL o emoji de la bandera
    private String continent;   // Continente
    private String currency;    // Código de moneda (ej: "ARS", "USD")
    private String phoneCode;   // Código telefónico (ej: "+54", "+55", "+1")
}
