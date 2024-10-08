package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.token;

import africa.nkwadoma.nkwadoma.application.ports.output.email.TokenGeneratorOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.validation.MiddleValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenGeneratorAdapter implements TokenGeneratorOutputPort {
    @Value("${jwt_secret}")
    private String secret;

    @Value("${expiration}")
    private Long expiration;

    @Override
    public String generateToken(String email) throws MiddlException {
        MiddleValidator.validateEmail(email);
            Map<String, Object> claims = new HashMap<>();
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(email)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    @Override
    public String decodeJWT(String token) throws MiddlException{
        MiddleValidator.validateDataElement(token);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

}
