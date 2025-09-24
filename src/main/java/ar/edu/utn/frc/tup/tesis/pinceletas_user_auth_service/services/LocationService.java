package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.*;

import java.util.List;
public interface LocationService {
    /**
     * Obtiene todos los países disponibles
     */
    List<CountryDto> getAllCountries();

    /**
     * Obtiene los estados/provincias de un país
     */
    List<StateDto> getStatesByCountry(String countryCode);

    /**
     * Obtiene las ciudades de un estado/provincia
     */
    List<CityDto> getCitiesByState(String countryCode, String stateCode);

    /**
     * Busca países por nombre
     */
    List<CountryDto> searchCountries(String query);
}
