package co.unicauca.edu.microservicio.microservicio_usuarios.domain.models;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor 
public class User {
    private String tenantId;
    private String id;
    private String username;
    private String name;
    private String lastName;
    private int age;
    private Role role;
    private String email;
    private String password;

    public User(){
        
    }

    public boolean isAdministrator() { return role == Role.ADMINISTRATOR; }
    public boolean isClient() { return role == Role.CLIENT; }
    public boolean isGuide() { return role == Role.TOURIST_GUIDE; }

    public void changePassword(String newPassword) {
        this.password = Objects.requireNonNull(newPassword);
    }

}
