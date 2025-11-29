package co.unicauca.edu.microservicio.microservicio_usuarios.aplication.input;

import java.util.List;

import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.PasswordChange;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.User;

public interface UserManagementIntPort {
    public List<User> getAllUsers(String tenantId);
    public User getUserById(String id, String tenantId);
    public User updateUser(String id, User user, String tenantId);
    public void deleteUser(String id, String tenantId);

    public void changePassword(String userId, String tenantId, PasswordChange passwordChange);
}
