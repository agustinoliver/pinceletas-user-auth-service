package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;

import java.util.Optional;

public interface UserService {
    UserEntity register(RegisterUserRequest request);
    UserEntity findByEmail(String email);
    UserResponse getUserByEmail(String email);
    UserResponse updateUser(String email, UpdateUserRequest request);
    UserResponse updateUserAddress(String email, UpdateAddressRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    void deleteUser(String email);
    void deactivateUser(String email);
    UserEntity upsertFirebaseUser(String uid, String email, String displayName, String provider);
    UserEntity registerWithFirebase(String uid, String email, String displayName, String provider,
                                    String firstName, String lastName, String phoneNumber);
    Optional<UserEntity> findByFirebaseUid(String uid);
    boolean existsByEmail(String email);
}
