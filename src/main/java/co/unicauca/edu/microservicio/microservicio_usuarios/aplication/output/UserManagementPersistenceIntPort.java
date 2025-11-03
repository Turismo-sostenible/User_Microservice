package co.unicauca.edu.microservicio.microservicio_usuarios.aplication.output;

import java.util.List;
import java.util.Optional;

import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.User;

public interface UserManagementPersistenceIntPort {
    public boolean userExistsByUsername(String username, String tenantId);
    public boolean userExistsByEmail(String email, String tenantId);
    public Optional<User> findByEmail(String email, String tenantId);
    public User saveUser(User user);
    public List<User> getAllUsers(String tenantId);
    public Optional<User> getUserById(String id, String tenantId);
    public void deleteUser(String id, String tenantId);
}
