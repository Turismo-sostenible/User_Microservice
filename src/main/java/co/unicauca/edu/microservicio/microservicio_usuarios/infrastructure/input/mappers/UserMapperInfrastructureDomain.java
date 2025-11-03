package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.AuthTokens;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.AuthUser;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.Login;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.User;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest.LoginDTORequest;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTORequest.UserDTORequest;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTOResponse.AuthResponse;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.input.DTOResponse.UserDTOResponse;

@Mapper(componentModel = "spring")
public interface UserMapperInfrastructureDomain {
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "id", ignore = true)
    public User toDomain(UserDTORequest userDTO);

    public UserDTOResponse toDTO(User user);

    public List<UserDTOResponse> toDTOList(List<User> users);

    public Login toDomain(LoginDTORequest loginDTO);

    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    public AuthUser toAuth(LoginDTORequest login);
    
    public AuthResponse toDTO(AuthTokens authUser);
}
