package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "Locations", description = "API para formularios de dirección")
public class LocationController {
    private final LocationService locationService;

    @GetMapping("/countries")
    @Operation(summary = "Obtener países", description = "Lista todos los países para selector de formulario")
    public ResponseEntity<List<CountryDto>> getAllCountries() {
        return ResponseEntity.ok(locationService.getAllCountries());
    }

    @GetMapping("/countries/{countryCode}/states")
    @Operation(summary = "Obtener provincias/estados", description = "Lista provincias de un país específico")
    public ResponseEntity<List<StateDto>> getStatesByCountry(
            @Parameter(description = "Código del país (ej: AR, US, BR)")
            @PathVariable String countryCode) {
        return ResponseEntity.ok(locationService.getStatesByCountry(countryCode));
    }

    @GetMapping("/countries/{countryCode}/states/{stateCode}/cities")
    @Operation(summary = "Obtener ciudades", description = "Lista ciudades de una provincia específica")
    public ResponseEntity<List<CityDto>> getCitiesByState(
            @Parameter(description = "Código del país")
            @PathVariable String countryCode,
            @Parameter(description = "Código de la provincia/estado")
            @PathVariable String stateCode) {
        return ResponseEntity.ok(locationService.getCitiesByState(countryCode, stateCode));
    }

    @GetMapping("/countries/search")
    @Operation(summary = "Buscar países", description = "Busca países por nombre (para autocompletado)")
    public ResponseEntity<List<CountryDto>> searchCountries(
            @Parameter(description = "Texto a buscar")
            @RequestParam String query) {
        return ResponseEntity.ok(locationService.searchCountries(query));
    }
}
