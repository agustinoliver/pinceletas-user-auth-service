package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.*;

import java.util.List;
public interface LocationService {

    List<CountryDto> getAllCountries();
    List<StateDto> getStatesByCountry(String countryCode);
    List<CountryDto> searchCountries(String query);
}
