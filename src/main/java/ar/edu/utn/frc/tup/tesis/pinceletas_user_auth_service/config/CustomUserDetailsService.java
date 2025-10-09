package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.config;

import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementación personalizada del servicio de detalles de usuario para Spring Security.
 * Carga la información del usuario desde la base de datos y valida su estado de activación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService{

    /** Repositorio para acceder a los datos de usuarios en la base de datos. */
    private final UserRepository userRepository;

    /**
     * Carga un usuario por su email (username) para el proceso de autenticación.
     * Valida que el usuario exista y esté activo antes de permitir el acceso.
     *
     * @param email Email del usuario a cargar.
     * @return UserDetails con la información del usuario para Spring Security.
     * @throws UsernameNotFoundException Si el usuario no existe o está desactivado.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado: " + email);
                });
        if (!user.isActivo()) {
            log.warn("Intento de acceso con cuenta desactivada: {}", email);
            throw new UsernameNotFoundException("La cuenta está desactivada: " + email);
        }

        log.debug("Usuario cargado exitosamente: {} con rol: {}", email, user.getRole());

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword() != null ? user.getPassword() : "")
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                ))
                .accountExpired(false)
                .accountLocked(!user.isActivo())
                .credentialsExpired(false)
                .disabled(!user.isActivo())
                .build();
    }
}
