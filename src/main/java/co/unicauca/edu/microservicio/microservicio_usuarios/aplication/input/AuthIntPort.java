package co.unicauca.edu.microservicio.microservicio_usuarios.aplication.input;

import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.AuthTokens;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.Login;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.User;

public interface AuthIntPort {
    AuthTokens login(Login login);
    User createUser(User createUser, String tenantId);
    AuthTokens refreshToken(String refreshToken, String tenantId);
}
