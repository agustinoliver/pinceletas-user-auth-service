package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.external;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class RestCountriesApiResponse {
    private Name name;
    private String cca2;      // Código de 2 letras
    private String cca3;      // Código de 3 letras
    private String ccn3;      // Código numérico
    private List<String> capital;
    private String region;
    private String subregion;
    private Map<String, Currency> currencies;
    private String flag;      // Emoji de bandera
    private Map<String, String> flags; // URLs de banderas
    private Idd idd;         // Información de código telefónico

    @Data
    public static class Name {
        private String common;
        private String official;
        private Map<String, NativeName> nativeName;
    }

    @Data
    public static class NativeName {
        private String official;
        private String common;
    }

    @Data
    public static class Currency {
        private String name;
        private String symbol;
    }

    @Data
    public static class Idd {
        private String root;
        private List<String> suffixes;
    }
}
