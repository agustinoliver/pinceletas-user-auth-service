package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.Impl;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.CountryDto;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.StateDto;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.external.CountryStatesApiResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.external.RestCountriesApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LocationServiceImpl locationService;

    private RestCountriesApiResponse[] mockCountriesResponse;
    private CountryStatesApiResponse mockStatesResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(locationService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(locationService, "countriesStatesApiUrl",
                "https://countriesnow.space/api/v0.1/countries");
        ReflectionTestUtils.setField(locationService, "restCountriesApiUrl",
                "https://restcountries.com/v3.1");

        setupMockCountriesResponse();
        setupMockStatesResponse();
    }

    private void setupMockCountriesResponse() {
        RestCountriesApiResponse country1 = new RestCountriesApiResponse();
        RestCountriesApiResponse.Name name1 = new RestCountriesApiResponse.Name();
        name1.setCommon("Argentina");
        country1.setName(name1);
        country1.setCca2("AR");
        country1.setRegion("Americas");
        country1.setFlag("🇦🇷");

        RestCountriesApiResponse.Idd idd1 = new RestCountriesApiResponse.Idd();
        idd1.setRoot("+5");
        idd1.setSuffixes(Arrays.asList("4"));
        country1.setIdd(idd1);

        Map<String, RestCountriesApiResponse.Currency> currencies1 = new HashMap<>();
        RestCountriesApiResponse.Currency currency1 = new RestCountriesApiResponse.Currency();
        currency1.setName("Argentine peso");
        currency1.setSymbol("$");
        currencies1.put("ARS", currency1);
        country1.setCurrencies(currencies1);

        RestCountriesApiResponse country2 = new RestCountriesApiResponse();
        RestCountriesApiResponse.Name name2 = new RestCountriesApiResponse.Name();
        name2.setCommon("Brazil");
        country2.setName(name2);
        country2.setCca2("BR");
        country2.setRegion("Americas");
        country2.setFlag("🇧🇷");

        RestCountriesApiResponse.Idd idd2 = new RestCountriesApiResponse.Idd();
        idd2.setRoot("+5");
        idd2.setSuffixes(Arrays.asList("5"));
        country2.setIdd(idd2);

        Map<String, RestCountriesApiResponse.Currency> currencies2 = new HashMap<>();
        RestCountriesApiResponse.Currency currency2 = new RestCountriesApiResponse.Currency();
        currency2.setName("Brazilian real");
        currency2.setSymbol("R$");
        currencies2.put("BRL", currency2);
        country2.setCurrencies(currencies2);

        mockCountriesResponse = new RestCountriesApiResponse[]{country1, country2};
    }

    private void setupMockStatesResponse() {
        mockStatesResponse = new CountryStatesApiResponse();
        mockStatesResponse.setError(false);

        CountryStatesApiResponse.CountryData countryData = new CountryStatesApiResponse.CountryData();
        countryData.setName("Argentina");
        countryData.setIso2("AR");
        countryData.setIso3("ARG");

        CountryStatesApiResponse.StateData state1 = new CountryStatesApiResponse.StateData();
        state1.setId(1L);
        state1.setName("Córdoba");
        state1.setState_code("C");
        state1.setType("province");

        CountryStatesApiResponse.StateData state2 = new CountryStatesApiResponse.StateData();
        state2.setId(2L);
        state2.setName("Buenos Aires");
        state2.setState_code("B");
        state2.setType("province");

        countryData.setStates(Arrays.asList(state1, state2));
        mockStatesResponse.setData(Collections.singletonList(countryData));
    }

    @Test
    void getAllCountries_Success() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(RestCountriesApiResponse[].class)))
                .thenReturn(new ResponseEntity<>(mockCountriesResponse, HttpStatus.OK));

        // Act
        List<CountryDto> result = locationService.getAllCountries();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Argentina", result.get(0).getName());
        assertEquals("AR", result.get(0).getCode());
        assertEquals("Brazil", result.get(1).getName());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(RestCountriesApiResponse[].class));
    }

    @Test
    void getAllCountries_EmptyResponse() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(RestCountriesApiResponse[].class)))
                .thenReturn(new ResponseEntity<>(new RestCountriesApiResponse[]{}, HttpStatus.OK));

        // Act
        List<CountryDto> result = locationService.getAllCountries();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(RestCountriesApiResponse[].class));
    }

    @Test
    void getAllCountries_NullResponse() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(RestCountriesApiResponse[].class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // Act
        List<CountryDto> result = locationService.getAllCountries();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(RestCountriesApiResponse[].class));
    }

    @Test
    void getAllCountries_TimeoutException() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(RestCountriesApiResponse[].class)))
                .thenThrow(new ResourceAccessException("Connection timeout"));

        // Act
        List<CountryDto> result = locationService.getAllCountries();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(RestCountriesApiResponse[].class));
    }

    @Test
    void getAllCountries_GeneralException() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(RestCountriesApiResponse[].class)))
                .thenThrow(new RuntimeException("API Error"));

        // Act
        List<CountryDto> result = locationService.getAllCountries();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(RestCountriesApiResponse[].class));
    }

    @Test
    void getStatesByCountry_Success() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(CountryStatesApiResponse.class)))
                .thenReturn(new ResponseEntity<>(mockStatesResponse, HttpStatus.OK));

        // Act
        List<StateDto> result = locationService.getStatesByCountry("AR");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Buenos Aires", result.get(0).getName());
        assertEquals("Córdoba", result.get(1).getName());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(CountryStatesApiResponse.class));
    }

    @Test
    void getStatesByCountry_WithIso3Code() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(CountryStatesApiResponse.class)))
                .thenReturn(new ResponseEntity<>(mockStatesResponse, HttpStatus.OK));

        // Act
        List<StateDto> result = locationService.getStatesByCountry("ARG");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(CountryStatesApiResponse.class));
    }

    @Test
    void getStatesByCountry_CountryNotFound() {
        // Arrange
        CountryStatesApiResponse emptyResponse = new CountryStatesApiResponse();
        emptyResponse.setData(Collections.emptyList());

        when(restTemplate.getForEntity(anyString(), eq(CountryStatesApiResponse.class)))
                .thenReturn(new ResponseEntity<>(emptyResponse, HttpStatus.OK));

        // Act
        List<StateDto> result = locationService.getStatesByCountry("XX");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(CountryStatesApiResponse.class));
    }

    @Test
    void getStatesByCountry_NoStatesForCountry() {
        // Arrange
        CountryStatesApiResponse responseWithoutStates = new CountryStatesApiResponse();
        CountryStatesApiResponse.CountryData countryData = new CountryStatesApiResponse.CountryData();
        countryData.setIso2("AR");
        countryData.setStates(null);
        responseWithoutStates.setData(Collections.singletonList(countryData));

        when(restTemplate.getForEntity(anyString(), eq(CountryStatesApiResponse.class)))
                .thenReturn(new ResponseEntity<>(responseWithoutStates, HttpStatus.OK));

        // Act
        List<StateDto> result = locationService.getStatesByCountry("AR");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(CountryStatesApiResponse.class));
    }

    @Test
    void getStatesByCountry_ApiException() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(CountryStatesApiResponse.class)))
                .thenThrow(new RuntimeException("API Error"));

        // Act
        List<StateDto> result = locationService.getStatesByCountry("AR");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(CountryStatesApiResponse.class));
    }

    @Test
    void searchCountries_Success() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(RestCountriesApiResponse[].class)))
                .thenReturn(new ResponseEntity<>(mockCountriesResponse, HttpStatus.OK));

        // Act
        List<CountryDto> result = locationService.searchCountries("Arg");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Argentina", result.get(0).getName());
    }

    @Test
    void searchCountries_CaseInsensitive() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(RestCountriesApiResponse[].class)))
                .thenReturn(new ResponseEntity<>(mockCountriesResponse, HttpStatus.OK));

        // Act
        List<CountryDto> result = locationService.searchCountries("ARGENTINA");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Argentina", result.get(0).getName());
    }

    @Test
    void searchCountries_NoResults() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(RestCountriesApiResponse[].class)))
                .thenReturn(new ResponseEntity<>(mockCountriesResponse, HttpStatus.OK));

        // Act
        List<CountryDto> result = locationService.searchCountries("NonExistent");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void searchCountries_SearchByCode() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(RestCountriesApiResponse[].class)))
                .thenReturn(new ResponseEntity<>(mockCountriesResponse, HttpStatus.OK));

        // Act
        List<CountryDto> result = locationService.searchCountries("AR");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("AR", result.get(0).getCode());
    }
}