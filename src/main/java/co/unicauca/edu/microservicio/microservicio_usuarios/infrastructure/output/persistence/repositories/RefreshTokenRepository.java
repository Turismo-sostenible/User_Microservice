package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.output.persistence.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.output.persistence.documents.RefreshTokenDocument;

public interface RefreshTokenRepository extends MongoRepository<RefreshTokenDocument, String>  {
    Optional<RefreshTokenDocument> findByTokenAndTenantId(String token, String tenantId);
    void deleteByUserId(String userId);
    
}
