package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.output.persistence.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
// Índice 1: Garantiza que el username sea único por inquilino
@CompoundIndex(name = "tenant_username_unique", def = "{'tenantId': 1, 'username': 1}", unique = true)
// Índice 2: Garantiza que el email sea único por inquilino
@CompoundIndex(name = "tenant_email_unique", def = "{'tenantId': 1, 'email': 1}", unique = true)
public class UserDocument {
    @Indexed
    private String tenantId;
    @Id
    private String id;
    @Field("username")
    private String username;
    @Field("name")
    private String name;
    @Field("lastName")
    private String lastName;
    @Field("age")
    private int age;
    @Field("role")
    private Role role;
    @Field("email")
    private String email;
    @Field("password")
    private String password;
}
