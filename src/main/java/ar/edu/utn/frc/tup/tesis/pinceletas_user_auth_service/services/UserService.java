package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth.RegisterUserRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.ChangePasswordRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UpdateAddressRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UpdateUserRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UserResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import java.util.Optional;

/**
 * Servicio para gestionar operaciones CRUD de usuarios.
 * Proporciona métodos para registro, actualización, eliminación y gestión de perfiles de usuario.
 */
public interface UserService {

    /**
     * Registra un nuevo usuario con credenciales tradicionales.
     *
     * @param request Datos del usuario a registrar.
     * @return UserEntity con los datos del usuario creado.
     */
    UserEntity register(RegisterUserRequest request);

    /**
     * Busca un usuario por su email.
     *
     * @param email Email del usuario a buscar.
     * @return UserEntity encontrado.
     * @throws RuntimeException si el usuario no existe.
     */
    UserEntity findByEmail(String email);

    /**
     * Obtiene los datos completos del perfil de un usuario.
     *
     * @param email Email del usuario.
     * @return UserResponse con todos los datos del perfil.
     */
    UserResponse getUserByEmail(String email);

    /**
     * Actualiza los datos básicos del perfil de un usuario.
     *
     * @param email Email del usuario a actualizar.
     * @param request Nuevos datos del usuario.
     * @return UserResponse con los datos actualizados.
     */
    UserResponse updateUser(String email, UpdateUserRequest request);

    /**
     * Actualiza la dirección completa de un usuario.
     *
     * @param email Email del usuario.
     * @param request Nuevos datos de dirección.
     * @return UserResponse con los datos actualizados incluyendo la dirección.
     */
    UserResponse updateUserAddress(String email, UpdateAddressRequest request);

    /**
     * Cambia la contraseña de un usuario validando la contraseña actual.
     *
     * @param email Email del usuario.
     * @param request Datos para el cambio de contraseña.
     */
    void changePassword(String email, ChangePasswordRequest request);

    /**
     * Elimina permanentemente un usuario del sistema.
     *
     * @param email Email del usuario a eliminar.
     */
    void deleteUser(String email);

    /**
     * Desactiva la cuenta de un usuario (soft delete).
     *
     * @param email Email del usuario a desactivar.
     */
    void deactivateUser(String email);

    /**
     * Crea o actualiza un usuario basado en datos de Firebase.
     * Si el usuario existe por UID o email, lo actualiza; si no, lo crea.
     *
     * @param uid Identificador único de Firebase.
     * @param email Email del usuario.
     * @param displayName Nombre para mostrar.
     * @param provider Proveedor de autenticación.
     * @return UserEntity creado o actualizado.
     */
    UserEntity upsertFirebaseUser(String uid, String email, String displayName, String provider);

    /**
     * Registra un nuevo usuario con datos específicos de Firebase.
     *
     * @param uid Identificador único de Firebase.
     * @param email Email del usuario.
     * @param displayName Nombre para mostrar.
     * @param provider Proveedor de autenticación.
     * @param firstName Primer nombre (opcional).
     * @param lastName Apellido (opcional).
     * @param phoneNumber Teléfono (opcional).
     * @return UserEntity creado.
     */
    UserEntity registerWithFirebase(String uid, String email, String displayName, String provider,
                                    String firstName, String lastName, String phoneNumber);

    /**
     * Busca un usuario por su identificador único de Firebase.
     *
     * @param uid Identificador único de Firebase.
     * @return Optional con el usuario si existe.
     */
    Optional<UserEntity> findByFirebaseUid(String uid);

    /**
     * Verifica si existe un usuario con el email especificado.
     *
     * @param email Email a verificar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByEmail(String email);
}
