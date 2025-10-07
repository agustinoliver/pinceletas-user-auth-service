package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private MimeMessage mimeMessage;
    private String testEmail;
    private String testToken;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testToken = "123456";

        // Setup MimeMessage mock
        Session session = Session.getInstance(new Properties());
        mimeMessage = new MimeMessage(session);

        // Set field values using reflection
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@pinceletas.com");
        ReflectionTestUtils.setField(emailService, "emailUsername", "cbapinceletas@gmail.com");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendPasswordResetEmail_Success() {
        // Arrange
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail(testEmail, testToken));

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendPasswordResetEmail_CorrectRecipient() throws Exception {
        // Arrange
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        doNothing().when(mailSender).send(messageCaptor.capture());

        // Act
        emailService.sendPasswordResetEmail(testEmail, testToken);

        // Assert
        MimeMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals(testEmail, sentMessage.getAllRecipients()[0].toString());
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendPasswordResetEmail_ContainsToken() throws Exception {
        // Arrange
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        doNothing().when(mailSender).send(messageCaptor.capture());

        // Act
        emailService.sendPasswordResetEmail(testEmail, testToken);

        // Assert
        MimeMessage sentMessage = messageCaptor.getValue();
        String content = sentMessage.getContent().toString();
        assertTrue(content.contains(testToken));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendPasswordResetEmail_CorrectSubject() throws Exception {
        // Arrange
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        doNothing().when(mailSender).send(messageCaptor.capture());

        // Act
        emailService.sendPasswordResetEmail(testEmail, testToken);

        // Assert
        MimeMessage sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage.getSubject().contains("Recuperación de contraseña"));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendPasswordResetEmail_ThrowsMessagingException() {
        // Arrange
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP Error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> emailService.sendPasswordResetEmail(testEmail, testToken));
        assertTrue(exception.getMessage().contains("SMTP Error"));
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendPasswordResetEmail_SendFailure() {
        // Arrange
        doThrow(new RuntimeException("Failed to send email"))
                .when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> emailService.sendPasswordResetEmail(testEmail, testToken));
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendPasswordResetEmail_WithNullEmail() {
        // Act & Assert
        assertThrows(Exception.class,
                () -> emailService.sendPasswordResetEmail(null, testToken));
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void sendPasswordResetEmail_WithEmptyEmail() {
        // Act & Assert
        assertThrows(Exception.class,
                () -> emailService.sendPasswordResetEmail("", testToken));
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void sendPasswordResetEmail_WithNullToken() {
        // Arrange
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail(testEmail, null));

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendPasswordResetEmail_HTMLContent() throws Exception {
        // Arrange
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        doNothing().when(mailSender).send(messageCaptor.capture());

        // Act
        emailService.sendPasswordResetEmail(testEmail, testToken);

        // Assert
        MimeMessage sentMessage = messageCaptor.getValue();
        String content = sentMessage.getContent().toString();
        assertTrue(content.contains("<div"));
        assertTrue(content.contains("</div>"));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

}