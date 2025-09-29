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
    private String code;
    private String name;
    private String countryCode;
    private String type;
}
