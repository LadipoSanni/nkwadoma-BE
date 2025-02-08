package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import com.nimbusds.jwt.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    @Value("${keycloak.principal_attribute}")
    private String principalAttribute;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractRealmRoles(jwt).stream()).toList();

        String claimName = principalAttribute == null ? JWTClaimNames.SUBJECT : principalAttribute;
        return new JwtAuthenticationToken(jwt, authorities, jwt.getClaim(claimName));
    }

    private Collection<SimpleGrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null) return Collections.emptySet();
        Collection<String> realmRoles = getRolesFromRealm(realmAccess);
        if (realmRoles == null) return Collections.emptySet();
        return realmRoles.stream()
                .map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getRolesFromRealm(Map<String, Object> realmAccess) {
        return (Collection<String>) realmAccess.get("roles");
    }
}
