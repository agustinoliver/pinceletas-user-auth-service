package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.email = :email")
    void deleteByEmail(@Param("email") String email);
}
