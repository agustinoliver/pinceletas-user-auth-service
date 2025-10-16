package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.config;

import ar.edu.utn.frc.tup.tesis.pinceletas.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de Spring Security.
 * Define las reglas de autorización, filtros JWT y proveedores de autenticación.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    /** Filtro JWT personalizado para validar tokens en cada request. */
    private final JwtAuthenticationFilter jwtAuthFilter;

    /** Servicio para cargar detalles de usuarios desde la base de datos. */
    private final UserDetailsService userDetailsService;

    /**
     * Configura la cadena de filtros de seguridad y las reglas de autorización.
     * Define qué endpoints son públicos y cuáles requieren autenticación.
     *
     * @param http Objeto HttpSecurity para configurar la seguridad.
     * @return SecurityFilterChain configurada.
     * @throws Exception Si hay error en la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login",
                                "/api/auth/forgot-password", "/api/auth/reset-password").permitAll()
                        .requestMatchers("/api/auth/firebase/login", "/api/auth/firebase/register").permitAll()

                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
                                "/webjars/**", "/api-docs/**", "/swagger-ui.html").permitAll()

                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        .requestMatchers("/api/locations/**").permitAll()
                        .requestMatchers("/api/admin/locations/**").permitAll()

                        // 🔥 NUEVO: Permitir acceso público al endpoint de comunicación entre servicios
                        .requestMatchers(HttpMethod.GET, "/api/users/by-email").permitAll()

                        // 🔥 IMPORTANTE: Endpoint de reportes público para comunicación entre microservicios
                        .requestMatchers(HttpMethod.GET, "/api/reports/users/active-inactive").permitAll()

                        .requestMatchers(HttpMethod.DELETE, "/api/users/profile/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/profile/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile/**").authenticated()

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                );

        return http.build();
    }

    /**
     * Crea el codificador de contraseñas usando BCrypt.
     *
     * @return PasswordEncoder configurado con BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Crea el gestor de autenticación de Spring Security.
     *
     * @param config Configuración de autenticación de Spring.
     * @return AuthenticationManager configurado.
     * @throws Exception Si hay error al obtener el AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura el proveedor de autenticación DAO.
     * Conecta el UserDetailsService con el PasswordEncoder.
     *
     * @return AuthenticationProvider configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}