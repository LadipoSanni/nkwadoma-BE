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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.InvalidKeyException;
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
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
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
            throw new MeedlException("Time allocated for this action has expired. Please refresh.");
        } catch (SignatureException | MalformedJwtException exception ) {
            throw new MeedlException("You are not authorized to perform this action. Invalid signature");
        }
        if (expiration == null || claims.getExpiration().before(new Date())) {
            throw new MeedlException("Token has expired");
        }
        return claims;
    }
    public String decryptBvn(String encryptedBvn) throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        String secretKey = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";  // Shared secret key
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBvn);

        // Create a secret key from the string
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");

        // Initialize the cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        // Decrypt the data
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        log.info(new String(decryptedBytes));
        // Convert decrypted bytes back to string
        return new String(decryptedBytes);

    }
}
