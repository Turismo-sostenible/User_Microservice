package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.output.KeyProviderPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.output.PasswordEncoderIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.output.TokenIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.output.UserManagementPersistenceIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.usecases.AuthService;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.usecases.GetPublicKeyService;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.usecases.UserManagementService;

@Configuration
public class BeanConfiguration {

    @Bean
    public UserManagementService createBeanManagementUseCaseAdapter(UserManagementPersistenceIntPort userManagementIntPort, PasswordEncoderIntPort passwordEncoderIntPort){
        UserManagementService userManagementUseCaseAdapter = new UserManagementService(userManagementIntPort, passwordEncoderIntPort);
        return userManagementUseCaseAdapter;
    }

    @Bean
    public AuthService createBeanAuthUseCaseAdapter(TokenIntPort tokenIntPort, UserManagementPersistenceIntPort userManagementIntPort, PasswordEncoderIntPort passwordEncoderIntPort){
        AuthService authAdapter = new AuthService(userManagementIntPort, passwordEncoderIntPort, tokenIntPort);
        return authAdapter;
    }

    @Bean
    public GetPublicKeyService getPublicKeyUseCase(KeyProviderPort keyProviderPort) {
        return new GetPublicKeyService(keyProviderPort);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
