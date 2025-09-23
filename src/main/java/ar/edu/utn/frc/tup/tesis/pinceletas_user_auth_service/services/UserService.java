package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.dto.*;
import ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.model.UserEntity;
public interface UserService {
    UserEntity register(RegisterUserRequest request);
    UserEntity findByEmail(String email);
    UserResponse getUserByEmail(String email);
    UserResponse updateUser(String email, UpdateUserRequest request);
    UserResponse updateUserAddress(String email, UpdateAddressRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    void deleteUser(String email);
    void deactivateUser(String email);
}
