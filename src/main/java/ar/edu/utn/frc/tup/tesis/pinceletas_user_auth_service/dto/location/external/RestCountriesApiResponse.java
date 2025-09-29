package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.external;

import lombok.Data;
import java.util.List;
import java.util.Map;

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
