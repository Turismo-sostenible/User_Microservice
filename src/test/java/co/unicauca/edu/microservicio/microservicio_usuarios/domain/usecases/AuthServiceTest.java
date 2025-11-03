package co.unicauca.edu.microservicio.microservicio_usuarios.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.output.PasswordEncoderIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.output.TokenIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.output.UserManagementPersistenceIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.AuthTokens;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.AuthUser;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.RefreshToken;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.Role;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.User;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.Login;

//Activa Mockito en JUnit 5
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    //Mocks de las dependencias
    @Mock
    private UserManagementPersistenceIntPort userRepo;

    @Mock
    private PasswordEncoderIntPort passwordEncoder;

    @Mock
    private TokenIntPort tokenPort;

    //La notación @InjectMocks crea una instancia de AuthService e inyecta los mocks anteriores
    @InjectMocks
    private AuthService authService;

    /**
     * Establece los mocks antes de cada prueba
     * Es reemplementado con la anotación @ExtendWith(MockitoExtension.class)
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepo, passwordEncoder, tokenPort);
    }*/

    @Test
    void login_success_returnsTokens() {

        // -- 1. GIVEN --

        //Datos de entrada
        String tenantId = "tenant-test-01";
        Login login = new Login("usuario@test.com", "pass123");

        //Datos simulados de la base de datos y servicios externos
        User userFalse = new User();
        userFalse.setId("user-id-abc");
        userFalse.setEmail("usuario@test.com");
        userFalse.setPassword("hash-real-de-pass123");
        userFalse.setTenantId(tenantId);
        userFalse.setRole(Role.CLIENT);
        userFalse.setAge(21);
        userFalse.setUsername("usuario01");

        //Datos falsos que simulan la generación de tokens
        RefreshToken refreshTokenFalso = new RefreshToken(tenantId,"id-refresh-falso",UUID.randomUUID().toString(), "user-id-abc", Instant.now().plus(Duration.ofDays(7)));
        String jwtFalso = "jwt.token.falso.12345";

        //Cuando llamen a userExistsByEmail con los parámetros indicados, devuelve true
        when(userRepo.userExistsByEmail(login.getEmail(), tenantId))
            .thenReturn(true);
        //Cuando llamen a findByEmail con los parámetros indicados, devuelve el usuario simulado
        when(userRepo.findByEmail(login.getEmail(), tenantId))
            .thenReturn(Optional.of(userFalse));
        //Cuando llamen a matches con los parámetros indicados, devuelve true (la contraseña coincide)
        when(passwordEncoder.matches(login.getPassword(), userFalse.getPassword()))
            .thenReturn(true);
        //Cuando llamen a generateToken y generateRefreshToken, devuelve los tokens simulados
        when(tokenPort.generateToken(any(AuthUser.class)))
            .thenReturn(jwtFalso);
        when(tokenPort.generateRefreshToken(userFalse.getId(), tenantId))
            .thenReturn(refreshTokenFalso);

        // -- 2. WHEN --
        AuthTokens tokensObtenidos = authService.login(login, tenantId);

        // -- 3. THEN --
        // Verificar los valores de salida (Asserts de JUnit)
        assertNotNull(tokensObtenidos); // El resultado no debe ser nulo
        assertEquals(jwtFalso, tokensObtenidos.getAccessToken()); // El token JWT es el que programamos
        assertEquals(refreshTokenFalso.getToken(), tokensObtenidos.getRefreshToken()); // El refresh token es el que programamos

        // Verificar que los mocks fueron llamados (Verifies de Mockito)
        // Esto confirma que tu lógica SÍ llamó a los puertos correctos
        verify(userRepo, times(1)).findByEmail(login.getEmail(), tenantId);
        verify(passwordEncoder, times(1)).matches(login.getPassword(), "hash-real-de-pass123");
        verify(tokenPort, times(1)).generateToken(any(AuthUser.class));
    }

    @Test
    void login_CuandoUsuarioNoExiste_DebeLanzarExcepcion() {
        // --- 1. GIVEN ---
        Login loginRequest = new Login("usuario.inexistente@test.com", "pass123");
        String tenantId = "tenant-test-1";

        // Programar el Mock para que devuelva false
        when(userRepo.userExistsByEmail(loginRequest.getEmail(), tenantId))
            .thenReturn(false);

        // --- 2. WHEN & 3. THEN  ---
        // Verificamos que al llamar al método, se lance la excepción esperada
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            // Ejecutamos el método que debe fallar
            authService.login(loginRequest, tenantId);
        });

        // Opcional: Verificar que el mensaje de la excepción es el correcto
        assertEquals("El usuario no existe.", exception.getMessage());
    }

    @Test
    void login_CuandoPasswordEsIncorrecta_DebeLanzarExcepcion() {
        // --- 1. GIVEN ---
        Login loginRequest = new Login("usuario@test.com", "pass-incorrecta");
        String tenantId = "tenant-test-1";
        User usuarioFalso = new User(); 
        usuarioFalso.setPassword("hash-real-de-pass123");
        
        // Programar el Mock (Camino feliz hasta la contraseña)
        when(userRepo.userExistsByEmail(loginRequest.getEmail(), tenantId))
            .thenReturn(true);
        when(userRepo.findByEmail(loginRequest.getEmail(), tenantId))
            .thenReturn(Optional.of(usuarioFalso));
        
        // Programar el Mock para que la contraseña falle
        when(passwordEncoder.matches(loginRequest.getPassword(), usuarioFalso.getPassword()))
            .thenReturn(false); 

        // --- 2. WHEN & 3. THEN ---
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(loginRequest, tenantId);
        });

        assertEquals("Credenciales inválidas.", exception.getMessage());
    }

    @Test
    void createUser_ConDatosValidos_DebeGuardarUsuario() {
        // --- 1. GIVEN ---
        String tenantId = "tenant-123";
        User usuarioNuevo = new User(); // Asigna username, email, password sin hashear
        usuarioNuevo.setUsername("nuevoUser");
        usuarioNuevo.setEmail("nuevo@test.com");
        usuarioNuevo.setPassword("pass-sin-hashear");
        
        String passwordHasheada = "hash-generado-por-mock";
        
        // Programar mocks
        // 1. Asegurarse de que el usuario NO existe
        when(userRepo.userExistsByUsername(usuarioNuevo.getUsername(), tenantId)).thenReturn(false);
        when(userRepo.findByEmail(usuarioNuevo.getEmail(), tenantId)).thenReturn(Optional.empty());
        
        // 2. Simular el hasheo de la contraseña
        when(passwordEncoder.encode(usuarioNuevo.getPassword())).thenReturn(passwordHasheada);
        
        // 3. Simular la acción de guardar
        // any(User.class) funciona porque saveUser acepta el objeto User
        when(userRepo.saveUser(any(User.class))).thenReturn(usuarioNuevo); 

        // --- 2. WHEN ---
        User usuarioGuardado = authService.createUser(usuarioNuevo, tenantId);

        // --- 3. THEN ---
        assertNotNull(usuarioGuardado);
        // Verifica que la contraseña hasheada fue asignada
        assertEquals(passwordHasheada, usuarioGuardado.getPassword()); 
        // Verifica que el tenantId fue asignado
        assertEquals(tenantId, usuarioGuardado.getTenantId()); 
        
        // Verifica que el método save fue llamado 1 vez
        verify(userRepo, times(1)).saveUser(any(User.class));
    }

    @Test
    void createUser_CuandoUsernameYaExiste_DebeLanzarExcepcion() {
        // --- 1. GIVEN ---
        String tenantId = "tenant-123";
        User usuarioNuevo = new User();
        usuarioNuevo.setUsername("usuario-existente");
        
        // Programar el mock para que devuelva que el usuario SÍ existe
        when(userRepo.userExistsByUsername(usuarioNuevo.getUsername(), tenantId)).thenReturn(true);

        // --- 2. WHEN & 3. THEN ---
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.createUser(usuarioNuevo, tenantId);
        });
        
        assertEquals("El usuario ya existe.", exception.getMessage());
        // Verifica que saveUser NUNCA fue llamado
        verify(userRepo, never()).saveUser(any(User.class)); 
    }

    @Test
    void createUser_CuandoEmailYaExiste_DebeLanzarExcepcion() {
        
        // --- 1. GIVEN (Dado) ---
        String tenantId = "tenant-123";
        
        // Datos de entrada 
        User usuarioNuevo = new User();
        usuarioNuevo.setUsername("usuario-valido"); 
        usuarioNuevo.setEmail("email-existente@test.com");
        usuarioNuevo.setPassword("pass123");

        //Datos simulados (El usuario que ya existe en la BD con ese email)
        User usuarioExistente = new User();
        usuarioExistente.setEmail("email-existente@test.com");
        
        // Programar los Mocks (El "Guion")

        // Queremos que la primera validación (username) PASE (devuelve false)
        when(userRepo.userExistsByUsername(usuarioNuevo.getUsername(), tenantId))
            .thenReturn(false);
            
        // Queremos que la segunda validación (email) FALLE (devuelve un usuario)
        when(userRepo.findByEmail(usuarioNuevo.getEmail(), tenantId))
            .thenReturn(Optional.of(usuarioExistente));

        
        // --- 2. WHEN & 3. THEN (Cuando y Entonces) ---

        // Verificamos que se lanza la excepción correcta
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            // Ejecutamos el método que debe fallar
            authService.createUser(usuarioNuevo, tenantId);
        });

        // Verificamos el mensaje de error
        assertEquals("El correo ya está en uso.", exception.getMessage());

        // Verificamos las interacciones con los mocks
        // Se debe haber llamado a las dos validaciones
        verify(userRepo, times(1)).userExistsByUsername(usuarioNuevo.getUsername(), tenantId);
        verify(userRepo, times(1)).findByEmail(usuarioNuevo.getEmail(), tenantId);
        
        // NUNCA se debió intentar hashear la contraseña ni guardar el usuario
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepo, never()).saveUser(any(User.class));
    }

    @Test
    void refreshToken_ConTokenValido_DebeRetornarNuevosTokens() {
        // --- 1. GIVEN (Dado) ---
        String refreshTokenString = "token-de-refresco-valido";
        String tenantId = "tenant-123";
        String userId = "user-abc";
        String nuevoJwtFalso = "jwt.nuevo.token.98765";

        // 1a. Simular el RefreshToken (Mockeamos el objeto de dominio)
        // Usamos mock() para poder controlar el método isExpired()
        RefreshToken tokenMock = mock(RefreshToken.class); 
        
        // 1b. Simular el Usuario (POJO)
        User usuarioFalso = new User();
        usuarioFalso.setTenantId(tenantId);
        usuarioFalso.setId(userId);
        usuarioFalso.setRole(Role.CLIENT);

        // 2. Programar los Mocks (El "Guion")
        
        // Cuando el puerto busque el token, lo encontrará
        when(tokenPort.findByTokenAndTenant(refreshTokenString, tenantId))
            .thenReturn(Optional.of(tokenMock));
            
        // Cuando se verifique si está expirado, devolverá false
        when(tokenMock.isExpired()).thenReturn(false);
        
        // Cuando se pida el ID del usuario del token
        when(tokenMock.getUserId()).thenReturn(userId);
        
        // Cuando se busque al usuario en la BD, se encontrará
        when(userRepo.getUserById(userId, tenantId))
            .thenReturn(Optional.of(usuarioFalso));
            
        // Cuando se intente generar el nuevo Access Token
        when(tokenPort.generateToken(any(AuthUser.class)))
            .thenReturn(nuevoJwtFalso);

        // --- 2. WHEN (Cuando) ---
        AuthTokens resultado = authService.refreshToken(refreshTokenString, tenantId);

        // --- 3. THEN (Entonces) ---
        assertNotNull(resultado);
        assertEquals(nuevoJwtFalso, resultado.getAccessToken()); // Verificamos el nuevo Access Token
        
        // Verificamos que se llamó a los métodos correctos
        verify(tokenPort, times(1)).findByTokenAndTenant(refreshTokenString, tenantId);
        verify(userRepo, times(1)).getUserById(userId, tenantId);
        verify(tokenPort, times(1)).generateToken(any(AuthUser.class));
    }

    @Test
    void refreshToken_CuandoTokenEsInvalido_DebeLanzarExcepcion() {
        // --- 1. GIVEN ---
        String refreshTokenString = "token-falso-o-de-otro-tenant";
        String tenantId = "tenant-123";

        // Programar el mock: El token no se encuentra
        when(tokenPort.findByTokenAndTenant(refreshTokenString, tenantId))
            .thenReturn(Optional.empty());

        // --- 2. WHEN & 3. THEN ---
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.refreshToken(refreshTokenString, tenantId);
        });

        assertEquals("Token de refresco inválido.", exception.getMessage());
        
        // Verificamos que NUNCA se intentó buscar un usuario ni generar un token
        verify(userRepo, never()).getUserById(anyString(), anyString());
        verify(tokenPort, never()).generateToken(any(AuthUser.class));
    }

    @Test
    void refreshToken_CuandoTokenEstaExpirado_DebeLanzarExcepcion() {
        // --- 1. GIVEN ---
        String refreshTokenString = "token-valido-pero-expirado";
        String tenantId = "tenant-123";
        
        // Simular el RefreshToken
        RefreshToken tokenMock = mock(RefreshToken.class); 

        // Programar el mock: El token SÍ se encuentra
        when(tokenPort.findByTokenAndTenant(refreshTokenString, tenantId))
            .thenReturn(Optional.of(tokenMock));
            
        // Programar el mock: El token SÍ está expirado
        when(tokenMock.isExpired()).thenReturn(true);

        // --- 2. WHEN & 3. THEN ---
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.refreshToken(refreshTokenString, tenantId);
        });

        assertEquals("Token de refresco expirado.", exception.getMessage());

        // Verificamos que SÍ se buscó el token, pero NUNCA se buscó al usuario
        verify(tokenPort, times(1)).findByTokenAndTenant(refreshTokenString, tenantId);
        verify(userRepo, never()).getUserById(anyString(), anyString());
    }
}