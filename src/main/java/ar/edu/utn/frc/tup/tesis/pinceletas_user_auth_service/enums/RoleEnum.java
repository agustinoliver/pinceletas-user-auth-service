package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.enums;

/**
 * Enumeración que define los roles de usuario disponibles en el sistema.
 * Determina los permisos y nivel de acceso de cada usuario.
 */
public enum RoleEnum {
    /** Usuario estándar con permisos básicos para gestionar su propio perfil. */
    USER,
    /** Usuario administrador con permisos completos sobre todos los usuarios. */
    ADMIN
}
