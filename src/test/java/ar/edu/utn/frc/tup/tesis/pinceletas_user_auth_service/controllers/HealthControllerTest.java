package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.controllers;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.common.MessageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {
    @InjectMocks
    private HealthController healthController;

    @Test
    void health_ReturnsOkStatus() {
        // Act
        ResponseEntity<MessageResponse> response = healthController.health();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Application is running!", response.getBody().getMessage());
    }

    @Test
    void health_ResponseBodyNotNull() {
        // Act
        ResponseEntity<MessageResponse> response = healthController.health();

        // Assert
        assertNotNull(response.getBody());
    }

    @Test
    void health_CorrectMessageContent() {
        // Act
        ResponseEntity<MessageResponse> response = healthController.health();

        // Assert
        MessageResponse messageResponse = response.getBody();
        assertNotNull(messageResponse);
        assertTrue(messageResponse.getMessage().contains("running"));
    }
}