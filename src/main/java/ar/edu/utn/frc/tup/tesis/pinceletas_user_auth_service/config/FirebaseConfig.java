package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {
    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.credentials.json:#{null}}")
    private String firebaseCredentialsJson;

    @PostConstruct
    public void init() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            GoogleCredentials credentials;

            if (firebaseCredentialsJson != null && !firebaseCredentialsJson.isEmpty()) {
                InputStream credentialsStream = new ByteArrayInputStream(
                        firebaseCredentialsJson.getBytes(StandardCharsets.UTF_8)
                );
                credentials = GoogleCredentials.fromStream(credentialsStream);
            } else {
                try (InputStream serviceAccount = getClass()
                        .getClassLoader()
                        .getResourceAsStream("firebase-service-account.json")) {

                    if (serviceAccount == null) {
                        throw new RuntimeException("No se encontró firebase-service-account.json ni variables de entorno");
                    }
                    credentials = GoogleCredentials.fromStream(serviceAccount);
                }
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }
}