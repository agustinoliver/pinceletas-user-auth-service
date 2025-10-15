package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.Rabbit.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionEvent {

    private String titulo;
    private String mensaje;
    private String tipo;
    private Long usuarioId;
    private String metadata;
    private String targetRole;
}
