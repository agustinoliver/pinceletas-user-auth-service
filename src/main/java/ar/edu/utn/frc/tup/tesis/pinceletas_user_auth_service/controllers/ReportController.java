package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.reports.UserStatsReport;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para generación de reportes del sistema.
 * Proporciona endpoints para obtener estadísticas y métricas.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "API para reportes y estadísticas del sistema")
public class ReportController {

    /** Servicio para generación de reportes. */
    private final ReportService reportService;

    /**
     * Obtiene las estadísticas de usuarios activos e inactivos.
     * Este endpoint está diseñado para comunicación entre microservicios.
     *
     * @return UserStatsReport con los conteos de usuarios activos, inactivos y total.
     */
    @GetMapping("/users/active-inactive")
    @Operation(
            summary = "Obtener estadísticas de usuarios activos/inactivos",
            description = "Devuelve conteos de usuarios activos e inactivos para dashboards y reportes"
    )
    public ResponseEntity<UserStatsReport> getUserActiveInactiveStats() {
        UserStatsReport report = reportService.getUserActiveInactiveStats();
        return ResponseEntity.ok(report);
    }
}
