package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.PasswordResetToken;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository.PasswordResetTokenRepository;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {
    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private UserEntity userEntity;
    private PasswordResetToken resetToken;
    private String testEmail;
    private String testToken;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testToken = "123456";

        // Setup UserEntity
        userEntity = UserEntity.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email(testEmail)
                .password("encodedPassword")
                .activo(true)
                .build();

        // Setup PasswordResetToken
        resetToken = PasswordResetToken.builder()
                .id(1L)
                .token(testToken)
                .email(testEmail)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Test
    void initiatePasswordReset_Success() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));
        doNothing().when(tokenRepository).deleteByEmail(testEmail);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(resetToken);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

        // Act
        assertDoesNotThrow(() -> passwordResetService.initiatePasswordReset(testEmail));

        // Assert
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(tokenRepository, times(1)).deleteByEmail(testEmail);
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendPasswordResetEmail(eq(testEmail), anyString());
    }

    @Test
    void initiatePasswordReset_EmailNotFound() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passwordResetService.initiatePasswordReset(testEmail));
        assertTrue(exception.getMessage().contains("no se encuentra registrado"));
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(tokenRepository, never()).deleteByEmail(anyString());
        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void initiatePasswordReset_AccountDeactivated() {
        // Arrange
        userEntity.setActivo(false);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passwordResetService.initiatePasswordReset(testEmail));
        assertTrue(exception.getMessage().contains("desactivada"));
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(tokenRepository, never()).deleteByEmail(anyString());
        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void initiatePasswordReset_DeletesPreviousTokens() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));
        doNothing().when(tokenRepository).deleteByEmail(testEmail);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(resetToken);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

        // Act
        passwordResetService.initiatePasswordReset(testEmail);

        // Assert
        verify(tokenRepository, times(1)).deleteByEmail(testEmail);
    }

    @Test
    void initiatePasswordReset_TokenIsSixDigits() {
        // Arrange
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));
        doNothing().when(tokenRepository).deleteByEmail(testEmail);
        when(tokenRepository.save(tokenCaptor.capture())).thenReturn(resetToken);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

        // Act
        passwordResetService.initiatePasswordReset(testEmail);

        // Assert
        PasswordResetToken capturedToken = tokenCaptor.getValue();
        assertNotNull(capturedToken.getToken());
        assertEquals(6, capturedToken.getToken().length());
        assertTrue(capturedToken.getToken().matches("\\d{6}"));
    }

    @Test
    void initiatePasswordReset_EmailSendFailure() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));
        doNothing().when(tokenRepository).deleteByEmail(testEmail);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(resetToken);
        doThrow(new RuntimeException("SMTP Error"))
                .when(emailService).sendPasswordResetEmail(anyString(), anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passwordResetService.initiatePasswordReset(testEmail));
        assertTrue(exception.getMessage().contains("Error al enviar el email"));
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_Success() {
        // Arrange
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";

        when(tokenRepository.findByTokenAndUsedFalse(testToken)).thenReturn(Optional.of(resetToken));
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(newPassword, userEntity.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(resetToken);

        // Act
        assertDoesNotThrow(() -> passwordResetService.resetPassword(testToken, newPassword, confirmPassword));

        // Assert
        verify(tokenRepository, times(1)).findByTokenAndUsedFalse(testToken);
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_PasswordsMismatch() {
        // Arrange
        String newPassword = "newPassword123";
        String confirmPassword = "differentPassword";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passwordResetService.resetPassword(testToken, newPassword, confirmPassword));
        assertEquals("Las contraseñas no coinciden", exception.getMessage());
        verify(tokenRepository, never()).findByTokenAndUsedFalse(anyString());
    }

    @Test
    void resetPassword_InvalidToken() {
        // Arrange
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";

        when(tokenRepository.findByTokenAndUsedFalse(testToken)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passwordResetService.resetPassword(testToken, newPassword, confirmPassword));
        assertTrue(exception.getMessage().contains("inválido") || exception.getMessage().contains("utilizado"));
        verify(tokenRepository, times(1)).findByTokenAndUsedFalse(testToken);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void resetPassword_ExpiredToken() {
        // Arrange
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";

        resetToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByTokenAndUsedFalse(testToken)).thenReturn(Optional.of(resetToken));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passwordResetService.resetPassword(testToken, newPassword, confirmPassword));
        assertTrue(exception.getMessage().contains("expirado"));
        verify(tokenRepository, times(1)).findByTokenAndUsedFalse(testToken);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void resetPassword_UserNotFound() {
        // Arrange
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";

        when(tokenRepository.findByTokenAndUsedFalse(testToken)).thenReturn(Optional.of(resetToken));
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passwordResetService.resetPassword(testToken, newPassword, confirmPassword));
        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(tokenRepository, times(1)).findByTokenAndUsedFalse(testToken);
        verify(userRepository, times(1)).findByEmail(testEmail);
    }

    @Test
    void resetPassword_SameAsCurrentPassword() {
        // Arrange
        String newPassword = "currentPassword";
        String confirmPassword = "currentPassword";

        when(tokenRepository.findByTokenAndUsedFalse(testToken)).thenReturn(Optional.of(resetToken));
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(newPassword, userEntity.getPassword())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passwordResetService.resetPassword(testToken, newPassword, confirmPassword));
        assertTrue(exception.getMessage().contains("diferente a la actual"));
        verify(tokenRepository, times(1)).findByTokenAndUsedFalse(testToken);
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void resetPassword_MarksTokenAsUsed() {
        // Arrange
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);

        when(tokenRepository.findByTokenAndUsedFalse(testToken)).thenReturn(Optional.of(resetToken));
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(newPassword, userEntity.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(tokenRepository.save(tokenCaptor.capture())).thenReturn(resetToken);

        // Act
        passwordResetService.resetPassword(testToken, newPassword, confirmPassword);

        // Assert
        PasswordResetToken capturedToken = tokenCaptor.getValue();
        assertTrue(capturedToken.isUsed());
    }

    @Test
    void resetPassword_WithNullPassword() {
        // Arrange
        userEntity.setPassword(null);
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";

        when(tokenRepository.findByTokenAndUsedFalse(testToken)).thenReturn(Optional.of(resetToken));
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(resetToken);

        // Act
        assertDoesNotThrow(() -> passwordResetService.resetPassword(testToken, newPassword, confirmPassword));

        // Assert
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void cleanupExpiredTokens_Success() {
        // Arrange
        doNothing().when(tokenRepository).deleteExpiredTokens(any(LocalDateTime.class));

        // Act
        assertDoesNotThrow(() -> passwordResetService.cleanupExpiredTokens());

        // Assert
        verify(tokenRepository, times(1)).deleteExpiredTokens(any(LocalDateTime.class));
    }

    @Test
    void cleanupExpiredTokens_RepositoryException() {
        // Arrange
        doThrow(new RuntimeException("Database error"))
                .when(tokenRepository).deleteExpiredTokens(any(LocalDateTime.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> passwordResetService.cleanupExpiredTokens());
        verify(tokenRepository, times(1)).deleteExpiredTokens(any(LocalDateTime.class));
    }
}