package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.common.MessageResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterUserRequest registerRequest;
    private LoginRequest loginRequest;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private FirebaseLoginRequest firebaseLoginRequest;
    private FirebaseRegisterRequest firebaseRegisterRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        // Setup RegisterUserRequest
        registerRequest = new RegisterUserRequest();
        registerRequest.setNombre("Juan");
        registerRequest.setApellido("Pérez");
        registerRequest.setEmail("juan.perez@example.com");
        registerRequest.setTelefono("1234567890");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        // Setup LoginRequest
        loginRequest = new LoginRequest();
        loginRequest.setEmail("juan.perez@example.com");
        loginRequest.setPassword("password123");

        // Setup ForgotPasswordRequest
        forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail("juan.perez@example.com");

        // Setup ResetPasswordRequest
        resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setToken("123456");
        resetPasswordRequest.setNewPassword("newPassword123");
        resetPasswordRequest.setConfirmNewPassword("newPassword123");

        // Setup FirebaseLoginRequest
        firebaseLoginRequest = new FirebaseLoginRequest();
        firebaseLoginRequest.setFirebaseIdToken("firebase-token-123");

        // Setup FirebaseRegisterRequest
        firebaseRegisterRequest = new FirebaseRegisterRequest();
        firebaseRegisterRequest.setFirebaseIdToken("firebase-token-123");
        firebaseRegisterRequest.setFirstName("Juan");
        firebaseRegisterRequest.setLastName("Pérez");
        firebaseRegisterRequest.setPhoneNumber("1234567890");

        // Setup AuthResponse
        authResponse = new AuthResponse("jwt-token-123");
    }

    @Test
    void register_Success() {
        // Arrange
        when(authService.register(any(RegisterUserRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token-123", response.getBody().getToken());
        verify(authService, times(1)).register(any(RegisterUserRequest.class));
    }

    @Test
    void register_ServiceThrowsException() {
        // Arrange
        when(authService.register(any(RegisterUserRequest.class)))
                .thenThrow(new RuntimeException("El email ya está registrado"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authController.register(registerRequest));
        verify(authService, times(1)).register(any(RegisterUserRequest.class));
    }

    @Test
    void login_Success() {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token-123", response.getBody().getToken());
        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_InvalidCredentials() {
        // Arrange
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authController.login(loginRequest));
        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void forgotPassword_Success() {
        // Arrange
        doNothing().when(authService).forgotPassword(any(ForgotPasswordRequest.class));

        // Act
        ResponseEntity<MessageResponse> response = authController.forgotPassword(forgotPasswordRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("recibirás un token"));
        verify(authService, times(1)).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    void forgotPassword_EmailNotFound() {
        // Arrange
        doThrow(new RuntimeException("El email ingresado no se encuentra registrado en el sistema"))
                .when(authService).forgotPassword(any(ForgotPasswordRequest.class));

        // Act
        ResponseEntity<MessageResponse> response = authController.forgotPassword(forgotPasswordRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("no se encuentra registrado"));
        verify(authService, times(1)).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    void forgotPassword_AccountDeactivated() {
        // Arrange
        doThrow(new RuntimeException("La cuenta está desactivada"))
                .when(authService).forgotPassword(any(ForgotPasswordRequest.class));

        // Act
        ResponseEntity<MessageResponse> response = authController.forgotPassword(forgotPasswordRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("desactivada"));
        verify(authService, times(1)).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    void resetPassword_Success() {
        // Arrange
        doNothing().when(authService).resetPassword(any(ResetPasswordRequest.class));

        // Act
        ResponseEntity<MessageResponse> response = authController.resetPassword(resetPasswordRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Contraseña restablecida exitosamente", response.getBody().getMessage());
        verify(authService, times(1)).resetPassword(any(ResetPasswordRequest.class));
    }

    @Test
    void resetPassword_InvalidToken() {
        // Arrange
        doThrow(new RuntimeException("Token inválido"))
                .when(authService).resetPassword(any(ResetPasswordRequest.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authController.resetPassword(resetPasswordRequest));
        verify(authService, times(1)).resetPassword(any(ResetPasswordRequest.class));
    }

    @Test
    void loginWithFirebase_Success() {
        // Arrange
        when(authService.loginWithFirebase(any(FirebaseLoginRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.loginWithFirebase(firebaseLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token-123", response.getBody().getToken());
        verify(authService, times(1)).loginWithFirebase(any(FirebaseLoginRequest.class));
    }

    @Test
    void loginWithFirebase_InvalidToken() {
        // Arrange
        when(authService.loginWithFirebase(any(FirebaseLoginRequest.class)))
                .thenThrow(new RuntimeException("Token de Firebase inválido"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authController.loginWithFirebase(firebaseLoginRequest));
        verify(authService, times(1)).loginWithFirebase(any(FirebaseLoginRequest.class));
    }

    @Test
    void registerWithFirebase_Success() {
        // Arrange
        when(authService.registerWithFirebase(any(FirebaseRegisterRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.registerWithFirebase(firebaseRegisterRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token-123", response.getBody().getToken());
        verify(authService, times(1)).registerWithFirebase(any(FirebaseRegisterRequest.class));
    }

    @Test
    void registerWithFirebase_EmailAlreadyExists() {
        // Arrange
        when(authService.registerWithFirebase(any(FirebaseRegisterRequest.class)))
                .thenThrow(new RuntimeException("El email ya está registrado"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authController.registerWithFirebase(firebaseRegisterRequest));
        verify(authService, times(1)).registerWithFirebase(any(FirebaseRegisterRequest.class));
    }
}