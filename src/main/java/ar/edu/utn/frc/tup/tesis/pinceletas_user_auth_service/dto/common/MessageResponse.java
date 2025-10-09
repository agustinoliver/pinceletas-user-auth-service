package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta genérica para operaciones que devuelven un mensaje de estado.
 * Utilizado para confirmaciones y respuestas simples del servidor.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {

    /** Mensaje descriptivo del resultado de la operación. */
    private String message;

    /**
     * Método factory para crear instancias de MessageResponse.
     *
     * @param message Texto del mensaje a incluir en la respuesta.
     * @return Nueva instancia de MessageResponse con el mensaje especificado.
     */
    public static MessageResponse of(String message) {
        return new MessageResponse(message);
    }
}
