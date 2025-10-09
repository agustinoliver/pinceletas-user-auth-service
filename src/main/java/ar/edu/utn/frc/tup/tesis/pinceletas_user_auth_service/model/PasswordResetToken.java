package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un token de recuperación de contraseña.
 * Almacena tokens temporales para el proceso de reset de contraseñas.
 */
@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    /** Identificador único del token en la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Token único de 6 dígitos para recuperación de contraseña. */
    @Column(nullable = false, unique = true)
    private String token;

    /** Email del usuario asociado a este token. */
    @Column(nullable = false)
    private String email;

    /** Fecha y hora de expiración del token (15 minutos después de la creación). */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /** Indica si el token ya fue utilizado para restablecer una contraseña. */
    @Column(nullable = false)
    private boolean used = false;

    /** Fecha y hora de creación del token. */
    @Column(nullable = false)
    private LocalDateTime createdDate;
}
