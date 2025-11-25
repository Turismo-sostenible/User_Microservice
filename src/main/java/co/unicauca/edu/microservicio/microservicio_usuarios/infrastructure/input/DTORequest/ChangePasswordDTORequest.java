package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDTORequest {
    @NotBlank(message = "El id del usuario es obligatoria")
    private String userId;
    @NotBlank(message = "El tenantId es obligatorio")
    private String tenantId;
    @NotBlank(message = "La contraseña actual es obligatoria")  
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String currentPassword;
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String newPassword;
}
