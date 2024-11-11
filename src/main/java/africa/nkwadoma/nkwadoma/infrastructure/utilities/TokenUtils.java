package africa.nkwadoma.nkwadoma.infrastructure.utilities;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.*;
import io.jsonwebtoken.security.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.security.*;
import java.util.*;

@Component
public class TokenUtils {
    @Value("${jwt_secret}")
    private String secret;

    @Value("${expiration}")
    private Long expiration;

    public String generateToken(String email) throws MeedlException {
        MeedlValidator.validateEmail(email);
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

    public String decodeJWT(String token) throws MeedlException {
        MeedlValidator.validateDataElement(token);
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException exception) {
            throw new MeedlException("Time allocated for this action has expired. Please refresh.");
        } catch (SignatureException exception) {
            throw new MeedlException("You are not authorized to perform this action. Invalid signature");
        }
        if (expiration == null || claims.getExpiration().before(new Date())) {
            throw new MeedlException("Token has expired");
        }
        return claims.getSubject();
    }
}
