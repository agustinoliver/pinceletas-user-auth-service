package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.external;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Respuesta de la API externa RestCountries para obtener información detallada de países.
 * Mapea la estructura JSON completa de la respuesta de RestCountries API.
 */
@Data
public class RestCountriesApiResponse {
    private Name name;
    private String cca2;
    private String cca3;
    private String ccn3;
    private List<String> capital;
    private String region;
    private String subregion;
    private Map<String, Currency> currencies;
    private String flag;
    private Map<String, String> flags;
    private Idd idd;

    /**
     * Información del nombre del país en diferentes formatos e idiomas.
     */
    @Data
    public static class Name {
        private String common;
        private String official;
        private Map<String, NativeName> nativeName;
    }

    /**
     * Nombre nativo del país en un idioma específico.
     */
    @Data
    public static class NativeName {
        private String official;
        private String common;
    }

    /**
     * Información de la moneda utilizada en el país.
     */
    @Data
    public static class Currency {
        private String name;
        private String symbol;
    }

    /**
     * Códigos de marcación internacional para el país.
     */
    @Data
    public static class Idd {
        private String root;
        private List<String> suffixes;
    }
}
