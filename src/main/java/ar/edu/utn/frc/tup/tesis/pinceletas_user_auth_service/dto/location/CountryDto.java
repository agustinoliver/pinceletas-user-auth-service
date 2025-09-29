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
    private String code;
    private String name;
    private String flag;
    private String continent;
    private String currency;
    private String phoneCode;
}
