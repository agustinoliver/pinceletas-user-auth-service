package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityDto {
    private String name;        // Nombre de la ciudad
    private String stateCode;   // Código del estado/provincia
    private String stateName;   // Nombre del estado/provincia
    private String countryCode; // Código del país
    private String countryName; // Nombre del país
    private Double latitude;    // Latitud (opcional)
    private Double longitude;   // Longitud (opcional)
}
