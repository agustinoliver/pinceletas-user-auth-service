package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.LoginRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.RegisterUserRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.AuthResponse;
public interface AuthService {
    AuthResponse register(RegisterUserRequest request);
    AuthResponse login(LoginRequest request);

}
