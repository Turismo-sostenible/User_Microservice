package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.RpcClient.Response;

import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.input.AuthIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.input.GetPublicKeyIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.AuthTokens;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.PasswordChange;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.Login;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.User;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.config.TenantContext;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest.ChangePasswordDTORequest;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest.LoginDTORequest;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest.RefreshTokenRequest;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest.UserDTORequest;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTOResponse.AuthResponse;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTOResponse.PublicKeyResponse;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTOResponse.UserDTOResponse;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.mappers.UserMapperInfrastructureDomain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthIntPort authIntPort;
    private final GetPublicKeyIntPort getPublicKeyIntPort;
    private final UserMapperInfrastructureDomain objMapper;

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
        UserDTOResponse userDTOResponse = objMapper.userToUserDTO(createdUser);
        ResponseEntity<UserDTOResponse> response = new ResponseEntity<UserDTOResponse>(userDTOResponse, HttpStatus.CREATED);
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDTORequest loginDTORequest){

        String tenantId = TenantContext.getCurrentTenant();

        Login login = objMapper.toDomain(loginDTORequest);
        AuthTokens token = authIntPort.login(login, tenantId);
        AuthResponse authResponse = objMapper.authTokensToAuthResponse(token);
        ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
        return response;
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        String refreshToken = request.getRefreshToken();

        String tenantId = TenantContext.getCurrentTenant();

        AuthTokens token = authIntPort.refreshToken(refreshToken, tenantId);
        AuthResponse authResponse = objMapper.authTokensToAuthResponse(token);
        ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
        return response;
    }
    
    @GetMapping("/public-key")
    public ResponseEntity<PublicKeyResponse> getPublicKey() {
        String key = this.getPublicKeyIntPort.getPublicKey();
        PublicKeyResponse response = new PublicKeyResponse("RSA256", "RSA", key);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordDTORequest request){
        PasswordChange changePassword = this.objMapper.toDomain(request);
        this.authIntPort.changePassword(request.getUserId(), request.getTenantId(), changePassword);
        return ResponseEntity.ok(Map.of("message", "Contraseña acutalizada correctamente"));
    }
    
}