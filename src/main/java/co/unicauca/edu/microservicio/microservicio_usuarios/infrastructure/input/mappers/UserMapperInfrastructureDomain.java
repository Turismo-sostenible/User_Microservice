package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.AuthTokens;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.AuthUser;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.PasswordChange;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.Login;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.User;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest.ChangePasswordDTORequest;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest.LoginDTORequest;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest.UserDTORequest;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTOResponse.AuthResponse;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTOResponse.UserDTOResponse;

@Mapper(componentModel = "spring")
public interface UserMapperInfrastructureDomain {

    //---Usuarios---
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "id", ignore = true)
    User toDomain(UserDTORequest userDTO);
    
    UserDTOResponse userToUserDTO(User user);

    List<UserDTOResponse> toDTOList(List<User> users);

    //--Login__
    Login toDomain(LoginDTORequest loginDTO);

    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    AuthUser toAuth(LoginDTORequest login);
    
    AuthResponse authTokensToAuthResponse(AuthTokens authUser);

    //--Password change--
    PasswordChange toDomain(ChangePasswordDTORequest changePassword);
}
