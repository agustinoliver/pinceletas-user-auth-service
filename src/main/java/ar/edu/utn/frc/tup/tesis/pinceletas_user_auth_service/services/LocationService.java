package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.location.*;

import java.util.List;

/**
 * Servicio para gestionar datos de ubicaciones geográficas (países y estados/provincias).
 * Utiliza APIs externas para obtener información actualizada y cachea los resultados.
 */
public interface LocationService {
    /**
     * Obtiene la lista completa de todos los países disponibles.
     * Los datos se obtienen de una API externa y se cachean para mejor rendimiento.
     *
     * @return Lista de CountryDto ordenada alfabéticamente por nombre.
     */
    List<CountryDto> getAllCountries();

    /**
     * Obtiene los estados/provincias de un país específico.
     * Los datos se cachean por código de país para optimizar consultas repetidas.
     *
     * @param countryCode Código ISO del país (ej: AR, US, BR).
     * @return Lista de StateDto con las divisiones administrativas del país.
     */
    List<StateDto> getStatesByCountry(String countryCode);

    /**
     * Busca países por nombre o código.
     * Útil para implementar funcionalidad de autocompletado en frontend.
     *
     * @param query Texto a buscar en nombre o código de país.
     * @return Lista de CountryDto que coinciden con la búsqueda (máximo 20 resultados).
     */
    List<CountryDto> searchCountries(String query);
}
