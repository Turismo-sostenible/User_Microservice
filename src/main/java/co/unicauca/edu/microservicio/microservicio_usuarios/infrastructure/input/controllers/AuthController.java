package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.input.AuthIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.input.GetPublicKeyIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.AuthTokens;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.Login;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.User;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.config.TenantContext;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest.LoginDTORequest;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest.RefreshTokenRequest;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest.UserDTORequest;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTOResponse.AuthResponse;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTOResponse.PublicKeyResponse;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTOResponse.UserDTOResponse;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.mappers.UserMapperInfrastructureDomain;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthIntPort authIntPort;
    private final GetPublicKeyIntPort getPublicKeyIntPort;
    private final UserMapperInfrastructureDomain objMapper;

    private final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthIntPort authIntPort, GetPublicKeyIntPort getPublicKeyIntPort, UserMapperInfrastructureDomain objMapper) {
        this.authIntPort = authIntPort;
        this.getPublicKeyIntPort = getPublicKeyIntPort;
        this.objMapper = objMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTOResponse> createUser(@RequestBody UserDTORequest userDTORequest) {
        User user = objMapper.toDomain(userDTORequest);

        String tenantId = TenantContext.getCurrentTenant();

        User createdUser = authIntPort.createUser(user, tenantId);
        UserDTOResponse userDTOResponse = objMapper.toDTO(createdUser);
        ResponseEntity<UserDTOResponse> response = new ResponseEntity<UserDTOResponse>(userDTOResponse, HttpStatus.CREATED);
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDTORequest loginDTORequest){
        Login login = objMapper.toDomain(loginDTORequest);
        AuthTokens token = authIntPort.login(login);
        AuthResponse authResponse = objMapper.toDTO(token);
        ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
        return response;
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        String refreshToken = request.getRefreshToken();

        String tenantId = TenantContext.getCurrentTenant();

        AuthTokens token = authIntPort.refreshToken(refreshToken, tenantId);
        AuthResponse authResponse = objMapper.toDTO(token);
        ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
        return response;
    }
    
    @GetMapping("/public-key")
    public ResponseEntity<PublicKeyResponse> getPublicKey() {
        String key = this.getPublicKeyIntPort.getPublicKey();
        PublicKeyResponse response = new PublicKeyResponse("RSA256", "RSA", key);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
}