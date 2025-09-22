package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.RegisterUserRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
public interface UserService {
    UserEntity register(RegisterUserRequest request);
    UserEntity findByEmail(String email);
}
