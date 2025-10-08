package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.Impl;

import ar.edu.utn.frc.tup.tesis.pinceletas.common.security.JwtService;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.enums.RoleEnum;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.PasswordResetService;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterUserRequest registerRequest;
    private LoginRequest loginRequest;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private FirebaseLoginRequest firebaseLoginRequest;
    private FirebaseRegisterRequest firebaseRegisterRequest;
    private UserEntity userEntity;

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

        // Setup UserEntity
        userEntity = UserEntity.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .telefono("1234567890")
                .password("encodedPassword")
                .role(RoleEnum.USER)
                .activo(true)
                .build();
    }

    @Test
    void register_Success() {
        // Arrange
        when(userService.register(any(RegisterUserRequest.class))).thenReturn(userEntity);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("jwt-token-123");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        verify(userService, times(1)).register(any(RegisterUserRequest.class));
        verify(jwtService, times(1)).generateToken(userEntity.getEmail(), userEntity.getRole().name());
    }

    @Test
    void register_UserServiceThrowsException() {
        // Arrange
        when(userService.register(any(RegisterUserRequest.class)))
                .thenThrow(new RuntimeException("El email ya está registrado"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userService, times(1)).register(any(RegisterUserRequest.class));
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_Success() {
        // Arrange
        when(userService.findByEmail(loginRequest.getEmail())).thenReturn(userEntity);
        when(passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("jwt-token-123");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        verify(userService, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), userEntity.getPassword());
        verify(jwtService, times(1)).generateToken(userEntity.getEmail(), userEntity.getRole().name());
    }

    @Test
    void login_AccountDeactivated() {
        // Arrange
        userEntity.setActivo(false);
        when(userService.findByEmail(loginRequest.getEmail())).thenReturn(userEntity);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));
        assertEquals("La cuenta está desactivada", exception.getMessage());
        verify(userService, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_InvalidPassword() {
        // Arrange
        when(userService.findByEmail(loginRequest.getEmail())).thenReturn(userEntity);
        when(passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));
        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(userService, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), userEntity.getPassword());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void forgotPassword_Success() {
        // Arrange
        doNothing().when(passwordResetService).initiatePasswordReset(forgotPasswordRequest.getEmail());

        // Act
        authService.forgotPassword(forgotPasswordRequest);

        // Assert
        verify(passwordResetService, times(1)).initiatePasswordReset(forgotPasswordRequest.getEmail());
    }

    @Test
    void forgotPassword_ServiceThrowsException() {
        // Arrange
        doThrow(new RuntimeException("El email no existe"))
                .when(passwordResetService).initiatePasswordReset(forgotPasswordRequest.getEmail());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.forgotPassword(forgotPasswordRequest));
        verify(passwordResetService, times(1)).initiatePasswordReset(forgotPasswordRequest.getEmail());
    }

    @Test
    void resetPassword_Success() {
        // Arrange
        doNothing().when(passwordResetService).resetPassword(
                resetPasswordRequest.getToken(),
                resetPasswordRequest.getNewPassword(),
                resetPasswordRequest.getConfirmNewPassword()
        );

        // Act
        authService.resetPassword(resetPasswordRequest);

        // Assert
        verify(passwordResetService, times(1)).resetPassword(
                resetPasswordRequest.getToken(),
                resetPasswordRequest.getNewPassword(),
                resetPasswordRequest.getConfirmNewPassword()
        );
    }

    @Test
    void resetPassword_InvalidToken() {
        // Arrange
        doThrow(new RuntimeException("Token inválido"))
                .when(passwordResetService).resetPassword(anyString(), anyString(), anyString());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.resetPassword(resetPasswordRequest));
        verify(passwordResetService, times(1)).resetPassword(anyString(), anyString(), anyString());
    }

    @Test
    void registerWithFirebase_Success() throws Exception {
        // Arrange
        FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);
        FirebaseToken mockFirebaseToken = mock(FirebaseToken.class);

        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "Juan Pérez");

        try (MockedStatic<FirebaseAuth> mockedStatic = mockStatic(FirebaseAuth.class)) {
            mockedStatic.when(FirebaseAuth::getInstance).thenReturn(mockFirebaseAuth);

            when(mockFirebaseAuth.verifyIdToken(anyString())).thenReturn(mockFirebaseToken);
            when(mockFirebaseToken.getUid()).thenReturn("firebase-uid-123");
            when(mockFirebaseToken.getEmail()).thenReturn("juan.perez@example.com");
            when(mockFirebaseToken.getClaims()).thenReturn(claims);
            when(userService.existsByEmail(anyString())).thenReturn(false);
            when(userService.registerWithFirebase(anyString(), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString())).thenReturn(userEntity);
            when(jwtService.generateToken(anyString(), anyString())).thenReturn("jwt-token-123");

            // Act
            AuthResponse response = authService.registerWithFirebase(firebaseRegisterRequest);

            // Assert
            assertNotNull(response);
            assertEquals("jwt-token-123", response.getToken());
            verify(userService, times(1)).existsByEmail(anyString());
            verify(userService, times(1)).registerWithFirebase(anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString(), anyString());
        }
    }

    @Test
    void registerWithFirebase_EmailAlreadyExists() throws Exception {
        // Arrange
        FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);
        FirebaseToken mockFirebaseToken = mock(FirebaseToken.class);

        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "Juan Pérez");

        try (MockedStatic<FirebaseAuth> mockedStatic = mockStatic(FirebaseAuth.class)) {
            mockedStatic.when(FirebaseAuth::getInstance).thenReturn(mockFirebaseAuth);

            when(mockFirebaseAuth.verifyIdToken(anyString())).thenReturn(mockFirebaseToken);
            when(mockFirebaseToken.getUid()).thenReturn("firebase-uid-123");
            when(mockFirebaseToken.getEmail()).thenReturn("juan.perez@example.com");
            when(mockFirebaseToken.getClaims()).thenReturn(claims);
            when(userService.existsByEmail(anyString())).thenReturn(true);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> authService.registerWithFirebase(firebaseRegisterRequest));
            assertTrue(exception.getMessage().contains("ya está registrado"));
        }
    }


    @Test
    void loginWithFirebase_Success() throws Exception {
        // Arrange
        FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);
        FirebaseToken mockFirebaseToken = mock(FirebaseToken.class);

        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "Juan Pérez");

        try (MockedStatic<FirebaseAuth> mockedStatic = mockStatic(FirebaseAuth.class)) {
            mockedStatic.when(FirebaseAuth::getInstance).thenReturn(mockFirebaseAuth);

            when(mockFirebaseAuth.verifyIdToken(anyString())).thenReturn(mockFirebaseToken);
            when(mockFirebaseToken.getUid()).thenReturn("firebase-uid-123");
            when(mockFirebaseToken.getEmail()).thenReturn("juan.perez@example.com");
            when(mockFirebaseToken.getClaims()).thenReturn(claims);
            when(userService.upsertFirebaseUser(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(userEntity);
            when(jwtService.generateToken(anyString(), anyString())).thenReturn("jwt-token-123");

            // Act
            AuthResponse response = authService.loginWithFirebase(firebaseLoginRequest);

            // Assert
            assertNotNull(response);
            assertEquals("jwt-token-123", response.getToken());
            verify(userService, times(1)).upsertFirebaseUser(anyString(), anyString(), anyString(), anyString());
        }
    }
    @Test
    void loginWithFirebase_AccountDeactivated() throws Exception {
        // Arrange
        userEntity.setActivo(false);
        FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);
        FirebaseToken mockFirebaseToken = mock(FirebaseToken.class);

        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "Juan Pérez");

        try (MockedStatic<FirebaseAuth> mockedStatic = mockStatic(FirebaseAuth.class)) {
            mockedStatic.when(FirebaseAuth::getInstance).thenReturn(mockFirebaseAuth);

            when(mockFirebaseAuth.verifyIdToken(anyString())).thenReturn(mockFirebaseToken);
            when(mockFirebaseToken.getUid()).thenReturn("firebase-uid-123");
            when(mockFirebaseToken.getEmail()).thenReturn("juan.perez@example.com");
            when(mockFirebaseToken.getClaims()).thenReturn(claims);
            when(userService.upsertFirebaseUser(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(userEntity);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> authService.loginWithFirebase(firebaseLoginRequest));
            assertTrue(exception.getMessage().contains("desactivada"));
        }
    }
}