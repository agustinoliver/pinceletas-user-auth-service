package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.PasswordResetToken;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository.PasswordResetTokenRepository;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public void initiatePasswordReset(String email) {
        log.info("Iniciando proceso de recuperación de contraseña para: {}", email);

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("Intento de recuperación con email no registrado: {}", email);
            // CAMBIO IMPORTANTE: Lanzar excepción específica
            throw new RuntimeException("El email ingresado no se encuentra registrado en el sistema");
        }

        UserEntity user = userOpt.get();

        if (!user.isActivo()) {
            log.warn("Intento de recuperación con cuenta desactivada: {}", email);
            throw new RuntimeException("La cuenta está desactivada. Contacta con soporte para más información.");
        }

        log.debug("Eliminando tokens anteriores para: {}", email);
        tokenRepository.deleteByEmail(email);

        String token = generateSecureToken();
        log.debug("Token generado para {}: {}", email, token);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .email(email)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .createdDate(LocalDateTime.now())
                .build();

        tokenRepository.save(resetToken);
        log.info("Token de recuperación guardado exitosamente para: {}", email);

        try {
            emailService.sendPasswordResetEmail(email, token);
            log.info("Email de recuperación enviado exitosamente a: {}", email);
        } catch (Exception e) {
            log.error("Error al enviar email de recuperación a {}: {}", email, e.getMessage());
            throw new RuntimeException("Error al enviar el email de recuperación. Intenta nuevamente en unos minutos.");
        }
    }
    public void resetPassword(String token, String newPassword, String confirmNewPassword) {
        log.info("Intentando restablecer contraseña con token: {}", token);

        if (!newPassword.equals(confirmNewPassword)) {
            log.warn("Las contraseñas no coinciden para el token: {}", token);
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        PasswordResetToken resetToken = tokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> {
                    log.error("Token inválido o ya utilizado: {}", token);
                    return new RuntimeException("El código ingresado es inválido o ya fue utilizado");
                });

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Token expirado: {}", token);
            throw new RuntimeException("El código ha expirado. Solicita uno nuevo.");
        }

        UserEntity user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado para el email: {}", resetToken.getEmail());
                    return new RuntimeException("Usuario no encontrado");
                });

        if (user.getPassword() != null && passwordEncoder.matches(newPassword, user.getPassword())) {
            log.warn("Intento de usar la misma contraseña actual para: {}", user.getEmail());
            throw new RuntimeException("La nueva contraseña debe ser diferente a la actual");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Contraseña actualizada exitosamente para: {}", user.getEmail());

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        log.info("Token marcado como usado: {}", token);
    }
    private String generateSecureToken() {
        int token = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(token);
    }
    @Transactional
    public void cleanupExpiredTokens() {
        log.debug("Iniciando limpieza de tokens expirados");
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.debug("Limpieza de tokens expirados completada");
    }
}
