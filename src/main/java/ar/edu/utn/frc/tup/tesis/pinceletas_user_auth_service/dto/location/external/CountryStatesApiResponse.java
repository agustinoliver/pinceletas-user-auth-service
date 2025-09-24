package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.external;

import lombok.Data;
import java.util.List;

@Data
public class CountryStatesApiResponse {
    private boolean error;
    private String msg;
    private List<CountryData> data;

    @Data
    public static class CountryData {
        private String name;
        private String iso3;
        private String iso2;
        private String numeric_code;
        private String phone_code;
        private String capital;
        private String currency;
        private String currency_name;
        private String currency_symbol;
        private String region;
        private String subregion;
        private List<StateData> states;
    }

    @Data
    public static class StateData {
        private Long id;
        private String name;
        private String state_code;
        private String latitude;
        private String longitude;
        private String type;
    }
}
