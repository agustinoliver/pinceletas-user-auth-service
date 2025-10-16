package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repositorio para gestionar operaciones de base de datos con usuarios.
 * Proporciona métodos para buscar, verificar existencia y gestionar usuarios.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long>{

    /**
     * Busca un usuario por su email.
     *
     * @param email Email del usuario a buscar.
     * @return Optional con el usuario si existe.
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email especificado.
     *
     * @param email Email a verificar.
     * @return true si existe un usuario con ese email, false en caso contrario.
     */
    boolean existsByEmail(String email);

    /**
     * Desactiva un usuario estableciendo su estado activo a false.
     * Operación batch para desactivación sin cargar la entidad completa.
     *
     * @param email Email del usuario a desactivar.
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.activo = false WHERE u.email = :email")
    void deactivateByEmail(@Param("email") String email);

    /**
     * Busca un usuario por su identificador único de Firebase.
     *
     * @param firebaseUid Identificador único proporcionado por Firebase.
     * @return Optional con el usuario si existe.
     */
    Optional<UserEntity> findByFirebaseUid(String firebaseUid);

    /**
     * Verifica si existe un usuario con el identificador de Firebase especificado.
     *
     * @param firebaseUid Identificador de Firebase a verificar.
     * @return true si existe un usuario con ese UID, false en caso contrario.
     */
    boolean existsByFirebaseUid(String firebaseUid);

    /**
     * Cuenta la cantidad de usuarios por estado de activación.
     *
     * @param activo Estado de activación a filtrar (true = activos, false = inactivos).
     * @return Cantidad de usuarios con el estado especificado.
     */
    long countByActivo(boolean activo);


    /**
     * Actualiza el estado activo de usuarios inactivos por más de 2 semanas.
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.activo = false WHERE u.lastActivityAt < :cutoffDate AND u.activo = true")
    int deactivateInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Encuentra usuarios activos con última actividad anterior a una fecha.
     */
    List<UserEntity> findByActivoTrueAndLastActivityAtBefore(LocalDateTime cutoffDate);

    /**
     * Obtiene todos los usuarios del sistema.
     */
    List<UserEntity> findAll();

}
