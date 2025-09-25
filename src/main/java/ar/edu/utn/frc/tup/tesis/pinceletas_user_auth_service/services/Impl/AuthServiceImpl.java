package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.Impl;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.AuthService;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.JwtService;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.PasswordResetService;
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
    private final PasswordResetService passwordResetService;

    @Override
    public AuthResponse register(RegisterUserRequest request) {
        UserEntity user = userService.register(request);
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        UserEntity user = userService.findByEmail(request.getEmail());

        if (!user.isActivo()) {
            throw new RuntimeException("La cuenta está desactivada");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        passwordResetService.initiatePasswordReset(request.getEmail());
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        passwordResetService.resetPassword(
                request.getToken(),
                request.getNewPassword(),
                request.getConfirmNewPassword()
        );
    }
    @Override
    public AuthResponse registerWithFirebase(FirebaseRegisterRequest request) {
        try {
            com.google.firebase.auth.FirebaseToken decoded =
                    com.google.firebase.auth.FirebaseAuth.getInstance()
                            .verifyIdToken(request.getFirebaseIdToken());

            String uid = decoded.getUid();
            String email = decoded.getEmail();
            String name = (String) decoded.getClaims().getOrDefault("name", email);

            // Verificar si ya existe
            if (userService.existsByEmail(email)) {
                throw new RuntimeException("El email ya está registrado");
            }

            UserEntity user = userService.registerWithFirebase(
                    uid, email, name, "firebase",
                    request.getFirstName(), request.getLastName(), request.getPhoneNumber()
            );

            // Usar tu JWT service existente
            String token = jwtService.generateToken(user.getEmail());
            return new AuthResponse(token);
        } catch (Exception e) {
            throw new RuntimeException("Error en el registro con Firebase: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse loginWithFirebase(FirebaseLoginRequest request) {
        try {
            com.google.firebase.auth.FirebaseToken decoded =
                    com.google.firebase.auth.FirebaseAuth.getInstance()
                            .verifyIdToken(request.getFirebaseIdToken());

            String uid = decoded.getUid();
            String email = decoded.getEmail();
            String name = (String) decoded.getClaims().getOrDefault("name", email);

            UserEntity user = userService.upsertFirebaseUser(uid, email, name, "firebase");

            if (!user.isActivo()) {
                throw new RuntimeException("La cuenta está desactivada");
            }

            // Usar tu JWT service existente
            String token = jwtService.generateToken(user.getEmail());
            return new AuthResponse(token);
        } catch (Exception e) {
            throw new RuntimeException("Error en el login con Firebase: " + e.getMessage());
        }
    }
}
