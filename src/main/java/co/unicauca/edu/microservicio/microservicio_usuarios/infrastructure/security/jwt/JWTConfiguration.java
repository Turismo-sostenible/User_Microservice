package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.security.jwt;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class JWTConfiguration {
    private final RsaPropierties rsaKeys;

    public JWTConfiguration(RsaPropierties rsaKeys) {
        this.rsaKeys = rsaKeys;
    }

    @Bean
    public JwtDecoder JwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }

    @Bean
    public JwtEncoder JwtEncoder() {
        RSAPrivateKey privateKey = parsePrivateKey(rsaKeys.privateKey());

        //JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    private RSAPrivateKey parsePrivateKey(String keyContent) {
        try {
            // Limpiamos cabeceras, pies y saltos de línea por si acaso
            String privateKeyPEM = keyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", ""); // Elimina espacios y saltos de línea

            // Decodificamos Base64
            byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

            // Reconstruimos la llave usando PKCS8 (el formato estándar para llaves privadas Java)
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo procesar la llave privada desde la configuración. Verifique que la variable de entorno contenga una llave RSA válida en formato PKCS#8.", e);
        }
    }
}
