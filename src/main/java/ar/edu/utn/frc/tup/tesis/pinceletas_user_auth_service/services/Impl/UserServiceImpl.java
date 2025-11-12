package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.Impl;


import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.auth.RegisterUserRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.ChangePasswordRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UpdateAddressRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UpdateUserRequest;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.user.UserResponse;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.enums.RoleEnum;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.repository.UserRepository;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementación del servicio de gestión de usuarios.
 * Proporciona operaciones CRUD completas para usuarios, incluyendo registro tradicional y con Firebase.
 * Maneja validaciones de negocio y transformaciones entre entidades y DTOs.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    /** Repositorio para operaciones de base de datos con usuarios. */
    private final UserRepository userRepository;
    /** Codificador de contraseñas para seguridad. */
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario con credenciales tradicionales.
     * Valida que el email no exista y que las contraseñas coincidan antes de crear el usuario.
     *
     * @param request Datos del usuario a registrar.
     * @return UserEntity con los datos del usuario creado.
     * @throws RuntimeException si el email ya está registrado o las contraseñas no coinciden.
     */
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
                .terminosAceptados(false)
                .build();

        return userRepository.save(user);
    }

    /**
     * Busca un usuario por su email.
     *
     * @param email Email del usuario a buscar.
     * @return UserEntity encontrado.
     * @throws RuntimeException si el usuario no existe.
     */
    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Obtiene los datos completos del perfil de un usuario.
     * Convierte la entidad UserEntity a UserResponse para exposición via API.
     *
     * @param email Email del usuario.
     * @return UserResponse con todos los datos del perfil.
     */
    @Override
    public UserResponse getUserByEmail(String email) {
        UserEntity user = findByEmail(email);
        return mapToUserResponse(user);
    }

    /**
     * Actualiza los datos básicos del perfil de un usuario.
     * Valida que el nuevo email no esté en uso por otro usuario.
     *
     * @param email Email actual del usuario a actualizar.
     * @param request Nuevos datos del usuario.
     * @return UserResponse con los datos actualizados.
     * @throws RuntimeException si el nuevo email ya está registrado por otro usuario.
     */
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

    /**
     * Actualiza la dirección completa de un usuario.
     * Permite actualizar todos los campos de dirección en una sola operación.
     *
     * @param email Email del usuario.
     * @param request Nuevos datos de dirección.
     * @return UserResponse con los datos actualizados incluyendo la dirección.
     */
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
        user.setManzana(request.getManzana());
        user.setLote(request.getLote());

        UserEntity updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    /**
     * Cambia la contraseña de un usuario validando la contraseña actual.
     * Verifica múltiples condiciones de seguridad antes de permitir el cambio.
     *
     * @param email Email del usuario.
     * @param request Datos para el cambio de contraseña.
     * @throws RuntimeException si la contraseña actual es incorrecta, las nuevas no coinciden,
     *         o la nueva contraseña es igual a la actual.
     */
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

    /**
     * Elimina permanentemente un usuario del sistema.
     * Operación irreversible que remueve completamente el usuario de la base de datos.
     *
     * @param email Email del usuario a eliminar.
     */
    @Override
    public void deleteUser(String email) {
        UserEntity user = findByEmail(email);
        userRepository.delete(user);
    }

    /**
     * Desactiva la cuenta de un usuario (soft delete).
     * El usuario no podrá iniciar sesión pero sus datos se mantienen en la base de datos.
     *
     * @param email Email del usuario a desactivar.
     */
    @Override
    public void deactivateUser(String email) {
        UserEntity user = findByEmail(email);
        user.setActivo(false);
        userRepository.save(user);
    }

    /**
     * Convierte una entidad UserEntity a un UserResponse para exposición via API.
     * Copia todos los campos relevantes incluyendo datos de dirección.
     *
     * @param user Entidad UserEntity a convertir.
     * @return UserResponse con los datos formateados para respuesta.
     */
    private UserResponse mapToUserResponse(UserEntity user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setNombre(user.getNombre());
        response.setApellido(user.getApellido());
        response.setEmail(user.getEmail());
        response.setTelefono(user.getTelefono());
        response.setRole(user.getRole());
        response.setActivo(user.isActivo());
        response.setTerminosAceptados(user.isTerminosAceptados());

        response.setCalle(user.getCalle());
        response.setNumero(user.getNumero());
        response.setCiudad(user.getCiudad());
        response.setPiso(user.getPiso());
        response.setBarrio(user.getBarrio());
        response.setPais(user.getPais());
        response.setProvincia(user.getProvincia());
        response.setCodigoPostal(user.getCodigoPostal());
        response.setManzana(user.getManzana());
        response.setLote(user.getLote());

        return response;
    }

    /**
     * Crea o actualiza un usuario basado en datos de Firebase (upsert operation).
     * Busca primero por UID de Firebase, luego por email, y si no existe crea uno nuevo.
     *
     * @param uid Identificador único de Firebase.
     * @param email Email del usuario.
     * @param displayName Nombre para mostrar.
     * @param provider Proveedor de autenticación.
     * @return UserEntity creado o actualizado.
     */
    @Override
    public UserEntity upsertFirebaseUser(String uid, String email, String displayName, String provider) {
        Optional<UserEntity> existingByUid = userRepository.findByFirebaseUid(uid);
        if (existingByUid.isPresent()) {
            UserEntity user = existingByUid.get();
            user.setDisplayName(displayName);
            user.setProvider(provider);
            return userRepository.save(user);
        }

        Optional<UserEntity> existingByEmail = userRepository.findByEmail(email);
        if (existingByEmail.isPresent()) {
            UserEntity user = existingByEmail.get();
            user.setFirebaseUid(uid);
            user.setDisplayName(displayName);
            user.setProvider(provider);
            return userRepository.save(user);
        }

        UserEntity newUser = UserEntity.builder()
                .firebaseUid(uid)
                .email(email)
                .displayName(displayName)
                .nombre(extractFirstName(displayName))
                .apellido(extractLastName(displayName))
                .telefono("")
                .provider(provider)
                .role(RoleEnum.USER)
                .activo(true)
                .terminosAceptados(false)
                .createdAt(java.time.Instant.now())
                .build();

        return userRepository.save(newUser);
    }

    /**
     * Registra un nuevo usuario con datos específicos de Firebase.
     * Crea un usuario completo con la información proporcionada.
     *
     * @param uid Identificador único de Firebase.
     * @param email Email del usuario.
     * @param displayName Nombre para mostrar.
     * @param provider Proveedor de autenticación.
     * @param firstName Primer nombre (opcional).
     * @param lastName Apellido (opcional).
     * @param phoneNumber Teléfono (opcional).
     * @return UserEntity creado.
     */
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
                .terminosAceptados(false)
                .createdAt(java.time.Instant.now())
                .build();

        return userRepository.save(user);
    }

    /**
     * Busca un usuario por su identificador único de Firebase.
     *
     * @param uid Identificador único de Firebase.
     * @return Optional con el usuario si existe.
     */
    @Override
    public Optional<UserEntity> findByFirebaseUid(String uid) {
        return userRepository.findByFirebaseUid(uid);
    }

    /**
     * Verifica si existe un usuario con el email especificado.
     *
     * @param email Email a verificar.
     * @return true si existe, false en caso contrario.
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    /**
     * Extrae el primer nombre del displayName de Firebase.
     * Si no se puede determinar, retorna "Usuario" como valor por defecto.
     *
     * @param displayName Nombre completo del usuario.
     * @return Primer nombre extraído.
     */
    private String extractFirstName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) return "Usuario";
        String[] parts = displayName.trim().split(" ");
        return parts.length > 0 ? parts[0] : "Usuario";
    }

    /**
     * Extrae el apellido del displayName de Firebase.
     * Si no se puede determinar, retorna "Firebase" como valor por defecto.
     *
     * @param displayName Nombre completo del usuario.
     * @return Apellido extraído.
     */
    private String extractLastName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) return "Firebase";
        String[] parts = displayName.trim().split(" ");
        return parts.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length)) : "Firebase";
    }

    @Override
    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }


    @Override
    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public UserEntity marcarTerminosAceptados(Long userId) {
        UserEntity user = findById(userId);
        user.setTerminosAceptados(true);
        return userRepository.save(user);
    }

    @Override
    public boolean verificarTerminosAceptados(Long userId) {
        UserEntity user = findById(userId);
        return user.isTerminosAceptados();
    }
}
