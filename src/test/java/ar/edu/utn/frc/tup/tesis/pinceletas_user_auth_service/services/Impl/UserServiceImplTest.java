package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.Impl;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth.RegisterUserRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.ChangePasswordRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UpdateAddressRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UpdateUserRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UserResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.enums.RoleEnum;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity userEntity;
    private RegisterUserRequest registerRequest;
    private UpdateUserRequest updateUserRequest;
    private UpdateAddressRequest updateAddressRequest;
    private ChangePasswordRequest changePasswordRequest;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";

        // Setup UserEntity
        userEntity = UserEntity.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email(testEmail)
                .telefono("1234567890")
                .password("encodedPassword")
                .role(RoleEnum.USER)
                .activo(true)
                .calle("Av. Principal")
                .numero("123")
                .ciudad("Córdoba")
                .pais("Argentina")
                .build();

        // Setup RegisterUserRequest
        registerRequest = new RegisterUserRequest();
        registerRequest.setNombre("Juan");
        registerRequest.setApellido("Pérez");
        registerRequest.setEmail(testEmail);
        registerRequest.setTelefono("1234567890");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        // Setup UpdateUserRequest
        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setNombre("Juan Carlos");
        updateUserRequest.setApellido("Pérez García");
        updateUserRequest.setEmail(testEmail);
        updateUserRequest.setTelefono("0987654321");

        // Setup UpdateAddressRequest
        updateAddressRequest = new UpdateAddressRequest();
        updateAddressRequest.setCalle("Av. Secundaria");
        updateAddressRequest.setNumero("456");
        updateAddressRequest.setCiudad("Buenos Aires");
        updateAddressRequest.setPais("Argentina");
        updateAddressRequest.setProvincia("Buenos Aires");
        updateAddressRequest.setCodigoPostal("1000");

        // Setup ChangePasswordRequest
        changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("password123");
        changePasswordRequest.setNewPassword("newPassword123");
        changePasswordRequest.setConfirmNewPassword("newPassword123");
    }

    @Test
    void register_Success() {
        // Arrange
        when(userRepository.existsByEmail(testEmail)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Act
        UserEntity result = userService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testEmail, result.getEmail());
        verify(userRepository, times(1)).existsByEmail(testEmail);
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void register_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register(registerRequest));
        assertEquals("El email ya está registrado", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(testEmail);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void register_PasswordsMismatch() {
        // Arrange
        registerRequest.setConfirmPassword("differentPassword");
        when(userRepository.existsByEmail(testEmail)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register(registerRequest));
        assertEquals("Las contraseñas no coinciden", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(testEmail);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void findByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));

        // Act
        UserEntity result = userService.findByEmail(testEmail);

        // Assert
        assertNotNull(result);
        assertEquals(testEmail, result.getEmail());
        verify(userRepository, times(1)).findByEmail(testEmail);
    }

    @Test
    void findByEmail_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.findByEmail(testEmail));
        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(testEmail);
    }

    @Test
    void getUserByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));

        // Act
        UserResponse result = userService.getUserByEmail(testEmail);

        // Assert
        assertNotNull(result);
        assertEquals(testEmail, result.getEmail());
        assertEquals("Juan", result.getNombre());
        assertEquals("Pérez", result.getApellido());
        verify(userRepository, times(1)).findByEmail(testEmail);
    }

    @Test
    void updateUser_EmailAlreadyExists() {
        // Arrange
        String newEmail = "newemail@example.com";
        updateUserRequest.setEmail(newEmail);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail(newEmail)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUser(testEmail, updateUserRequest));
        assertEquals("El nuevo email ya está registrado", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(userRepository, times(1)).existsByEmail(newEmail);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void updateUserAddress_Success() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.save(userCaptor.capture())).thenReturn(userEntity);

        // Act
        UserResponse result = userService.updateUserAddress(testEmail, updateAddressRequest);

        // Assert
        assertNotNull(result);
        UserEntity capturedUser = userCaptor.getValue();
        assertEquals("Av. Secundaria", capturedUser.getCalle());
        assertEquals("Buenos Aires", capturedUser.getCiudad());
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void changePassword_IncorrectCurrentPassword() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), userEntity.getPassword()))
                .thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.changePassword(testEmail, changePasswordRequest));
        assertEquals("La contraseña actual es incorrecta", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void changePassword_NewPasswordsMismatch() {
        // Arrange
        changePasswordRequest.setConfirmNewPassword("differentPassword");
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), userEntity.getPassword()))
                .thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.changePassword(testEmail, changePasswordRequest));
        assertEquals("Las nuevas contraseñas no coinciden", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void changePassword_SameAsCurrentPassword() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), userEntity.getPassword()))
                .thenReturn(true);
        when(passwordEncoder.matches(changePasswordRequest.getNewPassword(), userEntity.getPassword()))
                .thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.changePassword(testEmail, changePasswordRequest));
        assertEquals("La nueva contraseña debe ser diferente a la actual", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));

        // Act
        userService.deleteUser(testEmail);

        // Assert
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(userRepository, times(1)).delete(userEntity);
    }

    @Test
    void deactivateUser_Success() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userEntity));

        // Act
        userService.deactivateUser(testEmail);

        // Assert
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(userRepository, times(1)).save(userEntity);
        assertFalse(userEntity.isActivo());
    }

    @Test
    void upsertFirebaseUser_ExistingByUid() {
        // Arrange
        String uid = "firebase-uid-123";
        String email = "firebase@example.com";
        String displayName = "Firebase User";

        UserEntity firebaseUser = UserEntity.builder()
                .id(2L)
                .firebaseUid(uid)
                .email(email)
                .displayName("Old Display Name")
                .provider("google")
                .build();

        when(userRepository.findByFirebaseUid(uid)).thenReturn(Optional.of(firebaseUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(firebaseUser);

        // Act
        UserEntity result = userService.upsertFirebaseUser(uid, email, displayName, "firebase");

        // Assert
        assertNotNull(result);
        assertEquals(displayName, result.getDisplayName());
        verify(userRepository, times(1)).findByFirebaseUid(uid);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void upsertFirebaseUser_ExistingByEmail() {
        // Arrange
        String uid = "firebase-uid-123";
        String email = "existing@example.com";
        String displayName = "Firebase User";

        UserEntity existingUser = UserEntity.builder()
                .id(1L)
                .email(email)
                .nombre("Juan")
                .apellido("Pérez")
                .build();

        when(userRepository.findByFirebaseUid(uid)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(existingUser);

        // Act
        UserEntity result = userService.upsertFirebaseUser(uid, email, displayName, "firebase");

        // Assert
        assertNotNull(result);
        assertEquals(uid, result.getFirebaseUid());
        assertEquals(displayName, result.getDisplayName());
        verify(userRepository, times(1)).findByFirebaseUid(uid);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void upsertFirebaseUser_NewUser() {
        // Arrange
        String uid = "firebase-uid-123";
        String email = "newuser@example.com";
        String displayName = "New Firebase User";

        when(userRepository.findByFirebaseUid(uid)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserEntity result = userService.upsertFirebaseUser(uid, email, displayName, "firebase");

        // Assert
        assertNotNull(result);
        assertEquals(uid, result.getFirebaseUid());
        assertEquals(email, result.getEmail());
        assertEquals("New", result.getNombre());
        assertEquals("Firebase User", result.getApellido());
        verify(userRepository, times(1)).findByFirebaseUid(uid);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void registerWithFirebase_Success() {
        // Arrange
        String uid = "firebase-uid-123";
        String email = "firebase@example.com";
        String displayName = "Firebase User";
        String firstName = "John";
        String lastName = "Doe";
        String phoneNumber = "1234567890";

        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserEntity result = userService.registerWithFirebase(uid, email, displayName, "firebase",
                firstName, lastName, phoneNumber);

        // Assert
        assertNotNull(result);
        assertEquals(uid, result.getFirebaseUid());
        assertEquals(email, result.getEmail());
        assertEquals(firstName, result.getNombre());
        assertEquals(lastName, result.getApellido());
        assertEquals(phoneNumber, result.getTelefono());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void findByFirebaseUid_Success() {
        // Arrange
        String uid = "firebase-uid-123";
        when(userRepository.findByFirebaseUid(uid)).thenReturn(Optional.of(userEntity));

        // Act
        Optional<UserEntity> result = userService.findByFirebaseUid(uid);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userEntity, result.get());
        verify(userRepository, times(1)).findByFirebaseUid(uid);
    }

    @Test
    void existsByEmail_True() {
        // Arrange
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        // Act
        boolean result = userService.existsByEmail(testEmail);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(testEmail);
    }

    @Test
    void existsByEmail_False() {
        // Arrange
        when(userRepository.existsByEmail(testEmail)).thenReturn(false);

        // Act
        boolean result = userService.existsByEmail(testEmail);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail(testEmail);
    }
}