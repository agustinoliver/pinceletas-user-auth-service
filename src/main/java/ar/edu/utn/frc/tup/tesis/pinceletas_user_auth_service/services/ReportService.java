package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.reports.UserStatsReport;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio para generación de reportes y estadísticas de usuarios.
 * Proporciona datos para dashboards administrativos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    /** Repositorio para acceder a los datos de usuarios. */
    private final UserRepository userRepository;

    /**
     * Genera un reporte con las estadísticas de usuarios activos e inactivos.
     *
     * @return UserStatsReport con los conteos de usuarios activos, inactivos y total.
     */
    public UserStatsReport getUserActiveInactiveStats() {
        log.info("Generando reporte de estadísticas de usuarios activos/inactivos");

        long activeCount = userRepository.countByActivo(true);
        long inactiveCount = userRepository.countByActivo(false);
        long totalCount = activeCount + inactiveCount;

        log.debug("Reporte generado - Activos: {}, Inactivos: {}, Total: {}",
                activeCount, inactiveCount, totalCount);

        return new UserStatsReport(activeCount, inactiveCount, totalCount);
    }

}
