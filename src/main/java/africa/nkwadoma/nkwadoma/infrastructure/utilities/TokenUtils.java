package africa.nkwadoma.nkwadoma.infrastructure.utilities;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.*;
import io.jsonwebtoken.security.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.security.*;
import java.util.*;

@Slf4j
@Component
public class TokenUtils {
    @Value("${jwt_secret}")
    private String secret;

    @Value("${expiration}")
    private Long expiration;

    public String generateToken(String email) throws MeedlException {
        MeedlValidator.validateEmail(email);
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        return buildJwt(claims);
    }

    private String buildJwt(Map<String, Object> claims) {
        long oneYearInMillis = expiration * 1000L * 24 * 365;
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + oneYearInMillis))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String email, String id) throws MeedlException {
        MeedlValidator.validateEmail(email);
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("email", email);
        return buildJwt(claims);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String decodeJWTGetEmail(String token) throws MeedlException {
        MeedlValidator.validateDataElement(token);
        Claims claims;
        claims = getClaims(token);
        return claims.get("email").toString();
    }
    public String decodeJWTGetId(String token) throws MeedlException {
        MeedlValidator.validateDataElement(token);
        Claims claims;
        claims = getClaims(token);
        return claims.get("id").toString();
    }
    private Claims getClaims(String token) throws MeedlException {
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException exception) {
            log.error("Time allocated for this action has expired. Please refresh.");
            throw new MeedlException("Time allocated for this action has expired. Please refresh.");
        } catch (SignatureException | MalformedJwtException exception ) {
            log.error("You are not authorized to perform this action. Invalid signature");
            throw new MeedlException("You are not authorized to perform this action. Invalid signature");
        }
        if (expiration == null || claims.getExpiration().before(new Date())) {
            log.info("Token has expired");
            throw new MeedlException("Token has expired");
        }
        return claims;
    }
}
