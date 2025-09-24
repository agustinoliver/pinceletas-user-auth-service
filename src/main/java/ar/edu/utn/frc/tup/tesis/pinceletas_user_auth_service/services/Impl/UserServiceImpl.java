package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.Impl;


import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.enums.RoleEnum;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository.UserRepository;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserEntity register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
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

    @Override
    public UserResponse getUserByEmail(String email) {
        UserEntity user = findByEmail(email);
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse updateUser(String email, UpdateUserRequest request) {
        UserEntity user = findByEmail(email);

        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El nuevo email ya está registrado");
        }

        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setEmail(request.getEmail());
        user.setTelefono(request.getTelefono());

        UserEntity updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }
    @Override
    public UserResponse updateUserAddress(String email, UpdateAddressRequest request) {
        UserEntity user = findByEmail(email);

        user.setCalle(request.getCalle());
        user.setNumero(request.getNumero());
        user.setCiudad(request.getCiudad());
        user.setPiso(request.getPiso());
        user.setBarrio(request.getBarrio());
        user.setPais(request.getPais());
        user.setProvincia(request.getProvincia());
        user.setCodigoPostal(request.getCodigoPostal());

        UserEntity updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }
    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        UserEntity user = findByEmail(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("Las nuevas contraseñas no coinciden");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("La nueva contraseña debe ser diferente a la actual");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String email) {
        UserEntity user = findByEmail(email);
        userRepository.delete(user);
    }

    @Override
    public void deactivateUser(String email) {
        UserEntity user = findByEmail(email);
        user.setActivo(false);
        userRepository.save(user);
    }
    private UserResponse mapToUserResponse(UserEntity user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setNombre(user.getNombre());
        response.setApellido(user.getApellido());
        response.setEmail(user.getEmail());
        response.setTelefono(user.getTelefono());
        response.setRole(user.getRole());
        response.setActivo(user.isActivo());

        response.setCalle(user.getCalle());
        response.setNumero(user.getNumero());
        response.setCiudad(user.getCiudad());
        response.setPiso(user.getPiso());
        response.setBarrio(user.getBarrio());
        response.setPais(user.getPais());
        response.setProvincia(user.getProvincia());
        response.setCodigoPostal(user.getCodigoPostal());

        return response;
    }
    @Override
    public UserEntity upsertFirebaseUser(String uid, String email, String displayName, String provider) {
        // Buscar por Firebase UID primero
        Optional<UserEntity> existingByUid = userRepository.findByFirebaseUid(uid);
        if (existingByUid.isPresent()) {
            UserEntity user = existingByUid.get();
            user.setDisplayName(displayName);
            user.setProvider(provider);
            return userRepository.save(user);
        }

        // Buscar por email
        Optional<UserEntity> existingByEmail = userRepository.findByEmail(email);
        if (existingByEmail.isPresent()) {
            UserEntity user = existingByEmail.get();
            user.setFirebaseUid(uid);
            user.setDisplayName(displayName);
            user.setProvider(provider);
            return userRepository.save(user);
        }

        // Crear nuevo usuario Firebase
        UserEntity newUser = UserEntity.builder()
                .firebaseUid(uid)
                .email(email)
                .displayName(displayName)
                .nombre(extractFirstName(displayName))
                .apellido(extractLastName(displayName))
                .telefono("") // Se puede actualizar después
                .provider(provider)
                .role(RoleEnum.USER)
                .activo(true)
                .createdAt(java.time.Instant.now())
                .build();

        return userRepository.save(newUser);
    }

    @Override
    public UserEntity registerWithFirebase(String uid, String email, String displayName,
                                           String provider, String firstName, String lastName, String phoneNumber) {
        UserEntity user = UserEntity.builder()
                .firebaseUid(uid)
                .email(email)
                .displayName(displayName)
                .nombre(firstName != null ? firstName : extractFirstName(displayName))
                .apellido(lastName != null ? lastName : extractLastName(displayName))
                .telefono(phoneNumber != null ? phoneNumber : "")
                .provider(provider)
                .role(RoleEnum.USER)
                .activo(true)
                .createdAt(java.time.Instant.now())
                .build();

        return userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findByFirebaseUid(String uid) {
        return userRepository.findByFirebaseUid(uid);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Métodos auxiliares
    private String extractFirstName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) return "Usuario";
        String[] parts = displayName.trim().split(" ");
        return parts.length > 0 ? parts[0] : "Usuario";
    }

    private String extractLastName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) return "Firebase";
        String[] parts = displayName.trim().split(" ");
        return parts.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length)) : "Firebase";
    }
}
