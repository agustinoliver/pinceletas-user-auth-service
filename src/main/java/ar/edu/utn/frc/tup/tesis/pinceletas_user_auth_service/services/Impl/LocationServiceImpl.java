package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.Impl;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.external.CountryStatesApiResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.external.RestCountriesApiResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService{
    private final RestTemplate restTemplate;

    @Value("${app.location.api.countries-states.url:https://countriesnow.space/api/v0.1/countries}")
    private String countriesStatesApiUrl;

    @Value("${app.location.api.rest-countries.url:https://restcountries.com/v3.1}")
    private String restCountriesApiUrl;

    public LocationServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Cacheable(value = "countries", unless = "#result.isEmpty()")
    public List<CountryDto> getAllCountries() {
        log.info("Obteniendo lista de todos los países");

        try {
            ResponseEntity<RestCountriesApiResponse[]> response = restTemplate.getForEntity(
                    restCountriesApiUrl + "/all?fields=name,cca2,cca3,capital,region,subregion,currencies,flag,flags,idd",
                    RestCountriesApiResponse[].class
            );

            if (response.getBody() != null) {
                List<CountryDto> countries = Arrays.stream(response.getBody())
                        .map(this::mapToCountryDto)
                        .sorted(Comparator.comparing(CountryDto::getName))
                        .collect(Collectors.toList());

                log.info("Obtenidos {} países exitosamente", countries.size());
                return countries;
            }
        } catch (ResourceAccessException e) {
            log.error("Timeout al obtener países: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error al obtener países: {}", e.getMessage());
        }

        log.warn("No se pudieron obtener países de la API externa");
        return Collections.emptyList();
    }

    @Override
    @Cacheable(value = "states", key = "#countryCode", unless = "#result.isEmpty()")
    public List<StateDto> getStatesByCountry(String countryCode) {
        log.info("Obteniendo estados/provincias para país: {}", countryCode);

        try {
            ResponseEntity<CountryStatesApiResponse> response = restTemplate.getForEntity(
                    countriesStatesApiUrl + "/states",
                    CountryStatesApiResponse.class
            );

            if (response.getBody() != null && response.getBody().getData() != null) {
                Optional<CountryStatesApiResponse.CountryData> countryData = response.getBody().getData().stream()
                        .filter(country -> countryCode.equalsIgnoreCase(country.getIso2()) ||
                                countryCode.equalsIgnoreCase(country.getIso3()))
                        .findFirst();

                if (countryData.isPresent() && countryData.get().getStates() != null) {
                    List<StateDto> states = countryData.get().getStates().stream()
                            .map(state -> mapToStateDto(state, countryCode))
                            .sorted(Comparator.comparing(StateDto::getName))
                            .collect(Collectors.toList());

                    log.info("Obtenidos {} estados/provincias para {}", states.size(), countryCode);
                    return states;
                }
            }
        } catch (Exception e) {
            log.error("Error al obtener estados para {}: {}", countryCode, e.getMessage());
        }

        log.warn("No se pudieron obtener estados para el país: {}", countryCode);
        return Collections.emptyList();
    }

    @Override
    @Cacheable(value = "cities", key = "#countryCode + '_' + #stateCode", unless = "#result.isEmpty()")
    public List<CityDto> getCitiesByState(String countryCode, String stateCode) {
        log.info("Obteniendo ciudades para estado: {} del país: {}", stateCode, countryCode);

        try {
            ResponseEntity<CountryStatesApiResponse> response = restTemplate.getForEntity(
                    countriesStatesApiUrl + "/states",
                    CountryStatesApiResponse.class
            );

            if (response.getBody() != null && response.getBody().getData() != null) {
                Optional<CountryStatesApiResponse.CountryData> countryData = response.getBody().getData().stream()
                        .filter(country -> countryCode.equalsIgnoreCase(country.getIso2()) ||
                                countryCode.equalsIgnoreCase(country.getIso3()))
                        .findFirst();

                if (countryData.isPresent()) {
                    Optional<CountryStatesApiResponse.StateData> stateData = countryData.get().getStates().stream()
                            .filter(state -> stateCode.equalsIgnoreCase(state.getState_code()) ||
                                    stateCode.equalsIgnoreCase(state.getName()))
                            .findFirst();

                    if (stateData.isPresent() && stateData.get().getCities() != null) {
                        List<CityDto> cities = stateData.get().getCities().stream()
                                .map(city -> mapToCityDto(city, stateData.get(), countryData.get()))
                                .sorted(Comparator.comparing(CityDto::getName))
                                .collect(Collectors.toList());

                        log.info("Obtenidas {} ciudades para {}, {}", cities.size(), stateCode, countryCode);
                        return cities;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error al obtener ciudades para {}, {}: {}", stateCode, countryCode, e.getMessage());
        }

        log.warn("No se pudieron obtener ciudades para {}, {}", stateCode, countryCode);
        return Collections.emptyList();
    }

    @Override
    public List<CountryDto> searchCountries(String query) {
        log.info("Buscando países con query: {}", query);

        return getAllCountries().stream()
                .filter(country -> country.getName().toLowerCase().contains(query.toLowerCase()) ||
                        country.getCode().toLowerCase().contains(query.toLowerCase()))
                .limit(20)
                .collect(Collectors.toList());
    }

    // Métodos de mapeo privados
    private CountryDto mapToCountryDto(RestCountriesApiResponse country) {
        String phoneCode = "";
        if (country.getIdd() != null) {
            phoneCode = country.getIdd().getRoot();
            if (country.getIdd().getSuffixes() != null && !country.getIdd().getSuffixes().isEmpty()) {
                phoneCode += country.getIdd().getSuffixes().get(0);
            }
        }

        String currency = "";
        if (country.getCurrencies() != null && !country.getCurrencies().isEmpty()) {
            currency = country.getCurrencies().keySet().iterator().next();
        }

        return CountryDto.builder()
                .code(country.getCca2())
                .name(country.getName().getCommon())
                .flag(country.getFlag())
                .continent(country.getRegion())
                .currency(currency)
                .phoneCode(phoneCode)
                .build();
    }

    private StateDto mapToStateDto(CountryStatesApiResponse.StateData state, String countryCode) {
        return StateDto.builder()
                .code(state.getState_code())
                .name(state.getName())
                .countryCode(countryCode)
                .type(state.getType() != null ? state.getType() : "state")
                .build();
    }

    private CityDto mapToCityDto(CountryStatesApiResponse.CityData city,
                                 CountryStatesApiResponse.StateData state,
                                 CountryStatesApiResponse.CountryData country) {
        Double latitude = null;
        Double longitude = null;

        try {
            if (city.getLatitude() != null && !city.getLatitude().isEmpty()) {
                latitude = Double.parseDouble(city.getLatitude());
            }
            if (city.getLongitude() != null && !city.getLongitude().isEmpty()) {
                longitude = Double.parseDouble(city.getLongitude());
            }
        } catch (NumberFormatException e) {
            log.debug("No se pudieron parsear las coordenadas para {}", city.getName());
        }

        return CityDto.builder()
                .name(city.getName())
                .stateCode(state.getState_code())
                .stateName(state.getName())
                .countryCode(country.getIso2())
                .countryName(country.getName())
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
