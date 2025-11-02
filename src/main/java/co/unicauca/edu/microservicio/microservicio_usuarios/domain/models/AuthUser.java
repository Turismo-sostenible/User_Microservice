package co.unicauca.edu.microservicio.microservicio_usuarios.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
public class AuthUser {
    private String tenantId;
    private String id;
    private Role role;
}
