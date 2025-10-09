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

/**
 * Controlador REST para gestión de ubicaciones geográficas.
 * Proporciona endpoints para obtener países, provincias/estados y búsqueda de ubicaciones
 * para ser utilizados en formularios de dirección.
 */
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "Locations", description = "API para formularios de dirección")
public class LocationController {

    /** Servicio para obtener datos de ubicaciones desde APIs externas. */
    private final LocationService locationService;

    /**
     * Obtiene la lista completa de países disponibles.
     * Los datos están cacheados para mejorar el rendimiento.
     *
     * @return Lista de CountryDto con información de todos los países.
     */
    @GetMapping("/countries")
    @Operation(summary = "Obtener países", description = "Lista todos los países para selector de formulario")
    public ResponseEntity<List<CountryDto>> getAllCountries() {
        return ResponseEntity.ok(locationService.getAllCountries());
    }

    /**
     * Obtiene las provincias o estados de un país específico.
     * Los datos están cacheados por código de país.
     *
     * @param countryCode Código ISO del país (ej: AR, US, BR).
     * @return Lista de StateDto con las provincias/estados del país.
     */
    @GetMapping("/countries/{countryCode}/states")
    @Operation(summary = "Obtener provincias/estados", description = "Lista provincias de un país específico")
    public ResponseEntity<List<StateDto>> getStatesByCountry(
            @Parameter(description = "Código del país (ej: AR, US, BR)")
            @PathVariable String countryCode) {
        return ResponseEntity.ok(locationService.getStatesByCountry(countryCode));
    }

    /**
     * Busca países por nombre o código.
     * Útil para implementar autocompletado en formularios.
     *
     * @param query Texto a buscar en nombre o código del país.
     * @return Lista de CountryDto con los países que coinciden con la búsqueda (máximo 20).
     */
    @GetMapping("/countries/search")
    @Operation(summary = "Buscar países", description = "Busca países por nombre (para autocompletado)")
    public ResponseEntity<List<CountryDto>> searchCountries(
            @Parameter(description = "Texto a buscar")
            @RequestParam String query) {
        return ResponseEntity.ok(locationService.searchCountries(query));
    }
}
