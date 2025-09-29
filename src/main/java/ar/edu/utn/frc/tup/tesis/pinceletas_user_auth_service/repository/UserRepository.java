package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface UserRepository extends JpaRepository<UserEntity, Long>{
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    @Modifying
    @Query("UPDATE UserEntity u SET u.activo = false WHERE u.email = :email")
    void deactivateByEmail(@Param("email") String email);
    Optional<UserEntity> findByFirebaseUid(String firebaseUid);
    boolean existsByFirebaseUid(String firebaseUid);
}
