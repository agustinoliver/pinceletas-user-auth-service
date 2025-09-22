package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.Impl;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.AuthService;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.JwtService;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterUserRequest request) {
        UserEntity user = userService.register(request);
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        UserEntity user = userService.findByEmail(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}
