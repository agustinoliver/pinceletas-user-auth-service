package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.common.MessageResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.ChangePasswordRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UpdateAddressRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UpdateUserRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UserResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.enums.RoleEnum;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.UserService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse userResponse;
    private UpdateUserRequest updateUserRequest;
    private UpdateAddressRequest updateAddressRequest;
    private ChangePasswordRequest changePasswordRequest;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";

        // Setup UserResponse
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setNombre("Juan");
        userResponse.setApellido("Pérez");
        userResponse.setEmail(testEmail);
        userResponse.setTelefono("1234567890");
        userResponse.setRole(RoleEnum.USER);
        userResponse.setActivo(true);
        userResponse.setCalle("Av. Principal");
        userResponse.setNumero("123");
        userResponse.setCiudad("Córdoba");
        userResponse.setPais("Argentina");

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
        changePasswordRequest.setCurrentPassword("oldPassword123");
        changePasswordRequest.setNewPassword("newPassword123");
        changePasswordRequest.setConfirmNewPassword("newPassword123");
    }

    @Test
    void getUserProfile_Success() {
        // Arrange
        when(userService.getUserByEmail(testEmail)).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.getUserProfile(testEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testEmail, response.getBody().getEmail());
        assertEquals("Juan", response.getBody().getNombre());
        verify(userService, times(1)).getUserByEmail(testEmail);
    }

    @Test
    void getUserProfile_UserNotFound() {
        // Arrange
        when(userService.getUserByEmail(testEmail))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userController.getUserProfile(testEmail));
        verify(userService, times(1)).getUserByEmail(testEmail);
    }

    @Test
    void updateUserProfile_Success() {
        // Arrange
        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setNombre("Juan Carlos");
        updatedResponse.setApellido("Pérez García");
        updatedResponse.setEmail(testEmail);
        updatedResponse.setTelefono("0987654321");

        when(userService.updateUser(eq(testEmail), any(UpdateUserRequest.class)))
                .thenReturn(updatedResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.updateUserProfile(testEmail, updateUserRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan Carlos", response.getBody().getNombre());
        assertEquals("Pérez García", response.getBody().getApellido());
        verify(userService, times(1)).updateUser(eq(testEmail), any(UpdateUserRequest.class));
    }

    @Test
    void updateUserProfile_EmailAlreadyExists() {
        // Arrange
        when(userService.updateUser(eq(testEmail), any(UpdateUserRequest.class)))
                .thenThrow(new RuntimeException("El nuevo email ya está registrado"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userController.updateUserProfile(testEmail, updateUserRequest));
        verify(userService, times(1)).updateUser(eq(testEmail), any(UpdateUserRequest.class));
    }

    @Test
    void updateUserAddress_Success() {
        // Arrange
        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setCalle("Av. Secundaria");
        updatedResponse.setNumero("456");
        updatedResponse.setCiudad("Buenos Aires");

        when(userService.updateUserAddress(eq(testEmail), any(UpdateAddressRequest.class)))
                .thenReturn(updatedResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.updateUserAddress(testEmail, updateAddressRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Av. Secundaria", response.getBody().getCalle());
        assertEquals("Buenos Aires", response.getBody().getCiudad());
        verify(userService, times(1)).updateUserAddress(eq(testEmail), any(UpdateAddressRequest.class));
    }

    @Test
    void updateUserAddress_UserNotFound() {
        // Arrange
        when(userService.updateUserAddress(eq(testEmail), any(UpdateAddressRequest.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userController.updateUserAddress(testEmail, updateAddressRequest));
        verify(userService, times(1)).updateUserAddress(eq(testEmail), any(UpdateAddressRequest.class));
    }

    @Test
    void changePassword_Success() {
        // Arrange
        doNothing().when(userService).changePassword(eq(testEmail), any(ChangePasswordRequest.class));

        // Act
        ResponseEntity<MessageResponse> response = userController.changePassword(testEmail, changePasswordRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Contraseña cambiada exitosamente", response.getBody().getMessage());
        verify(userService, times(1)).changePassword(eq(testEmail), any(ChangePasswordRequest.class));
    }

    @Test
    void changePassword_IncorrectCurrentPassword() {
        // Arrange
        doThrow(new RuntimeException("La contraseña actual es incorrecta"))
                .when(userService).changePassword(eq(testEmail), any(ChangePasswordRequest.class));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userController.changePassword(testEmail, changePasswordRequest));
        verify(userService, times(1)).changePassword(eq(testEmail), any(ChangePasswordRequest.class));
    }

    @Test
    void changePassword_PasswordsMismatch() {
        // Arrange
        doThrow(new RuntimeException("Las nuevas contraseñas no coinciden"))
                .when(userService).changePassword(eq(testEmail), any(ChangePasswordRequest.class));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userController.changePassword(testEmail, changePasswordRequest));
        verify(userService, times(1)).changePassword(eq(testEmail), any(ChangePasswordRequest.class));
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        doNothing().when(userService).deleteUser(testEmail);

        // Act
        ResponseEntity<MessageResponse> response = userController.deleteUser(testEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Usuario eliminado exitosamente", response.getBody().getMessage());
        verify(userService, times(1)).deleteUser(testEmail);
    }

    @Test
    void deleteUser_UserNotFound() {
        // Arrange
        doThrow(new RuntimeException("Usuario no encontrado"))
                .when(userService).deleteUser(testEmail);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userController.deleteUser(testEmail));
        verify(userService, times(1)).deleteUser(testEmail);
    }

    @Test
    void deactivateUser_Success() {
        // Arrange
        doNothing().when(userService).deactivateUser(testEmail);

        // Act
        ResponseEntity<MessageResponse> response = userController.deactivateUser(testEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Usuario desactivado exitosamente", response.getBody().getMessage());
        verify(userService, times(1)).deactivateUser(testEmail);
    }

    @Test
    void deactivateUser_UserNotFound() {
        // Arrange
        doThrow(new RuntimeException("Usuario no encontrado"))
                .when(userService).deactivateUser(testEmail);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userController.deactivateUser(testEmail));
        verify(userService, times(1)).deactivateUser(testEmail);
    }
}