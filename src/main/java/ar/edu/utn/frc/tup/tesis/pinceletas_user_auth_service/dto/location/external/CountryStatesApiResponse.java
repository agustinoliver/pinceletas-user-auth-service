package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.external;

import lombok.Data;
import java.util.List;

/**
 * Respuesta de la API externa CountriesNow para obtener países y sus estados/provincias.
 * Mapea la estructura JSON completa de la respuesta de la API.
 */
@Data
public class CountryStatesApiResponse {

    /** Indica si la API devolvió un error. */
    private boolean error;
    /** Mensaje descriptivo de la respuesta de la API. */
    private String msg;
    /** Lista de datos de países con sus estados/provincias. */
    private List<CountryData> data;

    /**
     * Datos completos de un país específico.
     * Contiene información geográfica y lista de estados/provincias.
     */
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

    /**
     * Datos de un estado/provincia dentro de un país.
     * Contiene información geográfica y códigos de identificación.
     */
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
