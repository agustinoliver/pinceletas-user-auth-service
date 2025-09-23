package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.*;

public interface AuthService {
    AuthResponse register(RegisterUserRequest request);
    AuthResponse login(LoginRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);

}
