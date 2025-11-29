package co.unicauca.edu.microservicio.microservicio_usuarios.domain.usecases;

import java.util.List;
import java.util.Optional;

import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.input.UserManagementIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.output.PasswordEncoderIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.output.UserManagementPersistenceIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.PasswordChange;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.User;

public class UserManagementService implements UserManagementIntPort {

    private final UserManagementPersistenceIntPort userManagementIntPort;
    private final PasswordEncoderIntPort passwordEncoderIntPort;

    public UserManagementService(UserManagementPersistenceIntPort userManagementIntPort, PasswordEncoderIntPort passwordEncoderIntPort) {
        this.userManagementIntPort = userManagementIntPort;
        this.passwordEncoderIntPort = passwordEncoderIntPort;
    }

    @Override
    public List<User> getAllUsers(String tenantId) {
        List<User> users = userManagementIntPort.getAllUsers(tenantId);
        return users;
    }

    @Override
    public User getUserById(String id, String tenantId) {
        Optional<User> userOpt = userManagementIntPort.getUserById(id, tenantId);
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
    }

    @Override
    public User updateUser(String id, User user, String tenantId) {
        Optional<User> existingUserOpt = userManagementIntPort.getUserById(id, tenantId);
        if (!existingUserOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        User existingUser = existingUserOpt.get();
        existingUser.setId(id);
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        
        if (user.getLastName() != null) {
            existingUser.setLastName(user.getLastName());
        }
        
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getAge() > 0) { 
            existingUser.setAge(user.getAge());
        }
        return userManagementIntPort.saveUser(existingUser);
    }

    @Override
    public void deleteUser(String id, String tenantId) {
        if (!userManagementIntPort.getUserById(id, tenantId).isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        userManagementIntPort.deleteUser(id, tenantId);
    }

    @Override
    public void changePassword(String userId, String tenantId, PasswordChange passwordChange) {
        User user = userManagementIntPort.getUserById(userId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoderIntPort.matches(passwordChange.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }

        String newPasswordHash = passwordEncoderIntPort.encode(passwordChange.getNewPassword());

        user.setPassword(newPasswordHash);
        userManagementIntPort.saveUser(user);
    }
    
}
