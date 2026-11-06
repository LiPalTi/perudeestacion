package pe.upn.perudeestacion.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/** Genera el token JWT (RFC 7519) firmado con HS256 despues de un login correcto. */
@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long minutos;

    public TokenService(JwtEncoder jwtEncoder, @Value("${app.jwt.expiration-minutes:60}") long minutos) {
        this.jwtEncoder = jwtEncoder;
        this.minutos = minutos;
    }

    public String generar(Authentication auth) {
        Instant ahora = Instant.now();
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replace("ROLE_", ""))
                .toList();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("peru-de-estacion")
                .subject(auth.getName())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(minutos, ChronoUnit.MINUTES))
                .claim("roles", roles)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long segundosDeVida() {
        return minutos * 60;
    }
}
