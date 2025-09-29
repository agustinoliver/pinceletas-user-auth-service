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

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService{
    private final UserRepository userRepository;

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
