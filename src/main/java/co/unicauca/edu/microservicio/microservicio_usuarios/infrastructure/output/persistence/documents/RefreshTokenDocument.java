package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.output.persistence.documents;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "refresh_tokens")
@CompoundIndex(name = "user_tenant_token_idx", def = "{'user_id': 1, 'tenant_id': 1}", unique = true)
public class RefreshTokenDocument {
    @Id
    private String id;
    @Field("tenant_id")
    private String tenantId;
    @Field("token")
    @Indexed(unique = true)
    private String token;
    @Field("user_id")
    private String userId;
    @Field("expiry_date")
    private Instant expiryDate;
}
