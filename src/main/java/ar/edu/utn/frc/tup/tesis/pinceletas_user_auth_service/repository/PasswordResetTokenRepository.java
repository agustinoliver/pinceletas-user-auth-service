package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositorio para gestionar operaciones de base de datos con tokens de recuperación de contraseña.
 * Proporciona métodos para buscar, eliminar y gestionar tokens.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{

    /**
     * Busca un token activo (no utilizado) por su valor.
     *
     * @param token Valor del token a buscar.
     * @return Optional con el token si existe y no ha sido usado.
     */
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

    /**
     * Elimina todos los tokens que hayan expirado antes de la fecha especificada.
     * Operación de limpieza automática para mantener la base de datos optimizada.
     *
     * @param now Fecha y hora actual para comparar con la expiración.
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Elimina todos los tokens asociados a un email específico.
     * Útil para invalidar tokens anteriores cuando se genera uno nuevo.
     *
     * @param email Email del usuario cuyos tokens deben eliminarse.
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.email = :email")
    void deleteByEmail(@Param("email") String email);
}
