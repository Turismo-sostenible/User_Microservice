package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.output.persistence.adapters;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.output.UserManagementPersistenceIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.User;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.output.persistence.documents.UserDocument;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.output.persistence.repositories.UserRepository;

@Service
public class UserManagemenPersistencetImplAdapter implements UserManagementPersistenceIntPort {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserManagemenPersistencetImplAdapter(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public boolean userExistsByUsername(String username, String tenantId) {
        return userRepository.existsByUsernameAndTenantId(username, tenantId);
    }

    @Override
    @Transactional
    public Optional<User> findByEmail(String email, String tenantId) {
        Optional<UserDocument> userDocument = userRepository.findByEmailAndTenantId(email, tenantId);
        Optional<User> user = modelMapper.map(userDocument, new TypeToken<Optional<User>>() {}.getType());
        return user;
    }

    @Override
    public User saveUser(User user) {
        UserDocument userDocument = this.modelMapper.map(user, UserDocument.class);
        UserDocument savedUserDocument = userRepository.save(userDocument);
        User savedUser = this.modelMapper.map(savedUserDocument, User.class);
        return savedUser;
    }

    @Override
    public List<User> getAllUsers(String tenantId) {
        Iterable<UserDocument> userDocuments = userRepository.findAllByTenantId(tenantId);
        List<User> users = modelMapper.map(userDocuments, new TypeToken<List<User>>() {}.getType());
        return users;
    }

    @Override
    public Optional<User> getUserById(String id, String tenantId) {
        Optional<UserDocument> userDocument = userRepository.findByIdAndTenantId(id, tenantId);
        Optional<User> user = modelMapper.map(userDocument, new TypeToken<Optional<User>>() {}.getType());
        return user;
    }

    @Override
    public void deleteUser(String id, String tenantId) {
        this.userRepository.deleteByIdAndTenantId(id, tenantId);
    }

    @Override
    public boolean userExistsByEmail(String email, String tenantId) {
        return userRepository.existsByEmailAndTenantId(email, tenantId);
    }
    
}
