package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.CountryDto;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.StateDto;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationControllerTest {
    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    private List<CountryDto> countries;
    private List<StateDto> states;

    @BeforeEach
    void setUp() {
        // Setup Countries
        CountryDto country1 = CountryDto.builder()
                .code("AR")
                .name("Argentina")
                .flag("🇦🇷")
                .continent("Americas")
                .currency("ARS")
                .phoneCode("+54")
                .build();

        CountryDto country2 = CountryDto.builder()
                .code("US")
                .name("United States")
                .flag("🇺🇸")
                .continent("Americas")
                .currency("USD")
                .phoneCode("+1")
                .build();

        countries = Arrays.asList(country1, country2);

        // Setup States
        StateDto state1 = StateDto.builder()
                .code("C")
                .name("Córdoba")
                .countryCode("AR")
                .type("province")
                .build();

        StateDto state2 = StateDto.builder()
                .code("B")
                .name("Buenos Aires")
                .countryCode("AR")
                .type("province")
                .build();

        states = Arrays.asList(state1, state2);
    }

    @Test
    void getAllCountries_Success() {
        // Arrange
        when(locationService.getAllCountries()).thenReturn(countries);

        // Act
        ResponseEntity<List<CountryDto>> response = locationController.getAllCountries();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Argentina", response.getBody().get(0).getName());
        assertEquals("United States", response.getBody().get(1).getName());
        verify(locationService, times(1)).getAllCountries();
    }

    @Test
    void getAllCountries_EmptyList() {
        // Arrange
        when(locationService.getAllCountries()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<CountryDto>> response = locationController.getAllCountries();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(locationService, times(1)).getAllCountries();
    }

    @Test
    void getAllCountries_ServiceThrowsException() {
        // Arrange
        when(locationService.getAllCountries())
                .thenThrow(new RuntimeException("Error al obtener países"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> locationController.getAllCountries());
        verify(locationService, times(1)).getAllCountries();
    }

    @Test
    void getStatesByCountry_Success() {
        // Arrange
        String countryCode = "AR";
        when(locationService.getStatesByCountry(countryCode)).thenReturn(states);

        // Act
        ResponseEntity<List<StateDto>> response = locationController.getStatesByCountry(countryCode);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Córdoba", response.getBody().get(0).getName());
        assertEquals("Buenos Aires", response.getBody().get(1).getName());
        verify(locationService, times(1)).getStatesByCountry(countryCode);
    }

    @Test
    void getStatesByCountry_EmptyList() {
        // Arrange
        String countryCode = "XX";
        when(locationService.getStatesByCountry(countryCode)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<StateDto>> response = locationController.getStatesByCountry(countryCode);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(locationService, times(1)).getStatesByCountry(countryCode);
    }

    @Test
    void getStatesByCountry_InvalidCountryCode() {
        // Arrange
        String countryCode = "INVALID";
        when(locationService.getStatesByCountry(countryCode))
                .thenThrow(new RuntimeException("País no encontrado"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> locationController.getStatesByCountry(countryCode));
        verify(locationService, times(1)).getStatesByCountry(countryCode);
    }

    @Test
    void searchCountries_Success() {
        // Arrange
        String query = "Arg";
        List<CountryDto> filteredCountries = Collections.singletonList(countries.get(0));
        when(locationService.searchCountries(query)).thenReturn(filteredCountries);

        // Act
        ResponseEntity<List<CountryDto>> response = locationController.searchCountries(query);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Argentina", response.getBody().get(0).getName());
        verify(locationService, times(1)).searchCountries(query);
    }

    @Test
    void searchCountries_NoResults() {
        // Arrange
        String query = "NonExistent";
        when(locationService.searchCountries(query)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<CountryDto>> response = locationController.searchCountries(query);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(locationService, times(1)).searchCountries(query);
    }

    @Test
    void searchCountries_EmptyQuery() {
        // Arrange
        String query = "";
        when(locationService.searchCountries(query)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<CountryDto>> response = locationController.searchCountries(query);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(locationService, times(1)).searchCountries(query);
    }

    @Test
    void searchCountries_CaseInsensitive() {
        // Arrange
        String query = "ARGENTINA";
        List<CountryDto> filteredCountries = Collections.singletonList(countries.get(0));
        when(locationService.searchCountries(query)).thenReturn(filteredCountries);

        // Act
        ResponseEntity<List<CountryDto>> response = locationController.searchCountries(query);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(locationService, times(1)).searchCountries(query);
    }
}