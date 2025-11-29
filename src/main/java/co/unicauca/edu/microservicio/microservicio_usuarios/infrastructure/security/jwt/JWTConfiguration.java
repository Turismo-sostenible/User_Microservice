package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.security.jwt;

import java.nio.file.Files;
import java.nio.file.Paths;
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

    private RSAPrivateKey parsePrivateKey(String source) {
        try {
            String keyContent = source;

            // --- LÓGICA INTELIGENTE (NUEVA) ---
            // Verificamos si parece una ruta de archivo (tiene "/" o termina en .pem) y NO tiene saltos de línea
            boolean looksLikePath = !source.contains("\n") && (source.contains("/") || source.endsWith(".pem") || source.matches("^[a-zA-Z]:.*"));

            if (looksLikePath) {
                try {
                    // Intenta leer el archivo del disco
                    keyContent = new String(Files.readAllBytes(Paths.get(source)));
                } catch (Exception e) {
                    // Si falla (ej. en AWS pusiste la llave directa y por casualidad tiene un /),
                    // ignoramos el error y usamos el texto original.
                    System.out.println("Advertencia: Parecía ruta pero no se pudo leer. Usando contenido directo.");
                    keyContent = source;
                }
            }
            // ----------------------------------

            // --- LÓGICA DE LIMPIEZA (LA QUE YA TENÍAS) ---
            String privateKeyPEM = keyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", ""); // Elimina espacios y saltos de línea

            // Decodificamos Base64
            byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

            // Reconstruimos la llave
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo procesar la llave privada. Verifique si la variable de entorno es una ruta válida o el contenido PKCS#8.", e);
        }
    }
}
