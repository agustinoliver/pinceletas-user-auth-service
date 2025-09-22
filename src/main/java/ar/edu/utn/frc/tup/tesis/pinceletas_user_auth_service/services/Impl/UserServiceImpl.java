package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.Impl;


import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.RegisterUserRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.enums.RoleEnum;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository.UserRepository;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserEntity register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ya registrado");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        UserEntity user = UserEntity.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleEnum.USER)
                .activo(true)
                .build();

        return userRepository.save(user);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
