package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad principal que representa un usuario en el sistema.
 * Almacena toda la información personal, de autenticación y dirección del usuario.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    /** Identificador único del usuario en la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Primer nombre del usuario. */
    @Column(nullable = false)
    private String nombre;

    /** Apellido del usuario. */
    @Column(nullable = false)
    private String apellido;

    /** Email único del usuario, utilizado como username para login. */
    @Column(unique = true, nullable = false)
    private String email;

    /** Número de teléfono del usuario. */
    @Column(nullable = false)
    private String telefono;

    /** Contraseña encriptada del usuario (puede ser nula para usuarios de Firebase). */
    @Column(nullable = true)
    private String password;

    /** Rol del usuario que determina sus permisos en el sistema. */
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    /** Estado de activación de la cuenta (true = activa, false = desactivada). */
    private boolean activo = true;

    // Campos de dirección
    private String calle;
    private String numero;
    private String ciudad;
    private String piso;
    private String barrio;
    private String pais;
    private String provincia;
    private String codigoPostal;
    private String manzana;
    private String lote;

    /** Identificador único de Firebase para usuarios autenticados con proveedores externos. */
    @Column(unique = true, nullable = true)
    private String firebaseUid;
    /** Proveedor de autenticación (firebase, google, facebook, etc.). */
    private String provider;
    /** Nombre para mostrar obtenido del proveedor de autenticación. */
    private String displayName;
    /** Fecha y hora de creación del usuario en formato timestamp. */
    private java.time.Instant createdAt;

    /** Fecha y hora del último inicio de sesión del usuario. */
    @Column
    private LocalDateTime lastLoginAt;

    /** Fecha y hora de la última actividad del usuario en el sistema. */
    @Column
    private LocalDateTime lastActivityAt;

    /** Indica si el usuario aceptó los términos y condiciones */
    @Column(name = "terminos_aceptados", nullable = false)
    private boolean terminosAceptados = false;

}
