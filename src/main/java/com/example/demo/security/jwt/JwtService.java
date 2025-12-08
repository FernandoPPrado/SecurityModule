package com.example.demo.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${security.jwt.expiration-seconds}")
    private long expirationSeconds;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Gera um JWT contendo:
     * - sub: username (normalmente email)
     * - id: id do usuário
     * - scope: roles/authorities separados por espaço
     * - iat/exp: emissão e expiração
     */
    public String generateToken(UserDetails userDetails, Long userId) {
        Instant now = Instant.now();

        String scope = buildScope(userDetails.getAuthorities());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userDetails.getUsername())
                .claim("id", userId)
                .claim("scope", scope)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    /**
     * Verifica se o token é válido:
     * - Assinatura correta
     * - Não expirou
     * - Username bate com o UserDetails informado
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Jwt jwt = jwtDecoder.decode(token);

            String username = jwt.getSubject();
            Instant expiresAt = jwt.getExpiresAt();

            if (expiresAt == null || expiresAt.isBefore(Instant.now())) {
                return false;
            }

            return username != null && username.equals(userDetails.getUsername());
        } catch (JwtException e) {
            // token inválido (assinatura, formato, etc.)
            return false;
        }
    }

    public String extractUsername(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    public Long extractUserId(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        Number id = jwt.getClaim("id");
        return id != null ? id.longValue() : null;
    }

    private String buildScope(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }
}
