package co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.output.persistence.adapters;

import java.util.List;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import co.unicauca.edu.microservicio.microservicio_usuarios.aplication.output.TokenIntPort;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.AuthUser;
import co.unicauca.edu.microservicio.microservicio_usuarios.domain.models.RefreshToken;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.output.persistence.documents.RefreshTokenDocument;
import co.unicauca.edu.microservicio.microservicio_usuarios.infrastructure.output.persistence.repositories.RefreshTokenRepository;

@Service
public class TokenAdapter implements TokenIntPort{

    private final static Logger logger = LoggerFactory.getLogger(TokenAdapter.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final ModelMapper modelMapper;
    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    

    @Value("${application.security.jwt.expiration}")
    private Duration jwtExpiration;

    public TokenAdapter(JwtEncoder encoder, JwtDecoder decoder, RefreshTokenRepository refreshTokenRepository, ModelMapper modelMapper) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public String generateToken(AuthUser authUser) {
        Instant now = Instant.now();

        String tenantId = authUser.getTenantId();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("microservicio-usuarios")
                .issuedAt(now)
                .expiresAt(now.plus(jwtExpiration))
                .subject(authUser.getId())
                .claim("tenant_id", tenantId)
                .claim("roles", List.of(authUser.getRole().name()))
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            decoder.decode(token);
            return true;
        } catch (Exception e) {
            logger.error("Error validating token", e);
            throw new BadJwtException("Error mientras se valida el token");
        }
    }

    @Override
    public String getUserIdFromToken(String token) {
        Jwt jwt = decoder.decode(token);
        return jwt.getSubject();
    }

    @Override
    public RefreshToken generateRefreshToken(String userId, String tenantId) {
        this.refreshTokenRepository.deleteByUserId(userId);
        RefreshTokenDocument refreshTokenDocument = new RefreshTokenDocument();
        refreshTokenDocument.setUserId(userId);
        refreshTokenDocument.setExpiryDate(Instant.now().plus(Duration.ofDays(7)));
        refreshTokenDocument.setToken(UUID.randomUUID().toString());
        refreshTokenDocument.setTenantId(tenantId);
        RefreshTokenDocument saveRefreshTokenDocument = this.refreshTokenRepository.save(refreshTokenDocument);
        return this.modelMapper.map(saveRefreshTokenDocument, RefreshToken.class);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        Optional<RefreshTokenDocument> refreshTokenDocument = this.refreshTokenRepository.findByToken(token);
        return this.modelMapper.map(refreshTokenDocument, new TypeToken<Optional<RefreshToken>>() {}.getType());
    }
}
