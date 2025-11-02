package co.unicauca.edu.microservicio.microservicio_usuarios.domain.models;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class RefreshToken {
    private String tenantId;
    private String id;
    private String token;
    private String userId;
    private Instant expiryDate;
    
    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}
