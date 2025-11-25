package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.security.jwt;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rsa")
public record RsaPropierties(RSAPublicKey publicKey, String privateKey) {
    
}
