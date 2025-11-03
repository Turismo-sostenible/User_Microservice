package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.output.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.output.persistence.documents.UserDocument;

public interface UserRepository extends MongoRepository<UserDocument, String> {
    boolean existsByUsernameAndTenantId(String username, String tenantId);
    boolean existsByEmailAndTenantId(String email, String tenantId);
    Optional<UserDocument> findByEmailAndTenantId(String email, String tenantId);
    Optional<UserDocument> findByIdAndTenantId(String id, String tenantId);
    void deleteByIdAndTenantId(String id, String tenantId);
    List<UserDocument> findAllByTenantId(String tenantId);
}
