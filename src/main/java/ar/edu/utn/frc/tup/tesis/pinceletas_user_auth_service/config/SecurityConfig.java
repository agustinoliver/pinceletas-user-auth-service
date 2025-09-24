package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Endpoints de autenticación
                        .requestMatchers("/api/auth/register", "/api/auth/login",
                                "/api/auth/forgot-password", "/api/auth/reset-password").permitAll()
                        // NUEVOS endpoints Firebase
                        .requestMatchers("/api/auth/firebase/login", "/api/auth/firebase/register").permitAll()

                        // Documentación Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
                                "/webjars/**", "/api-docs/**", "/swagger-ui.html").permitAll()

                        // Health check
                        .requestMatchers("/health").permitAll()

                        // H2 Console
                        .requestMatchers("/h2-console/**").permitAll()

                        // APIs públicas
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/**").permitAll()

                        // APIs de ubicaciones (públicas para facilitar el uso)
                        .requestMatchers("/api/locations/**").permitAll()

                        // APIs administrativas (deberían requerir autenticación en producción)
                        .requestMatchers("/api/admin/locations/**").permitAll() // TODO: Cambiar a authenticated() en producción

                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}