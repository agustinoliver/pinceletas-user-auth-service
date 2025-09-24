package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateDto {
    private String code;        // Código del estado/provincia (ej: "C", "BA", "CA")
    private String name;        // Nombre del estado/provincia (ej: "CABA", "Buenos Aires", "California")
    private String countryCode; // Código del país al que pertenece
    private String type;        // Tipo: "state", "province", "region", etc.
}
