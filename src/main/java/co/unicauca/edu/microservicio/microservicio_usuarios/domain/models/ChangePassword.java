package co.unicauca.edu.microservicio.microservicio_usuarios.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChangePassword{
    private String currentPassword;
    private String newPassword;
}