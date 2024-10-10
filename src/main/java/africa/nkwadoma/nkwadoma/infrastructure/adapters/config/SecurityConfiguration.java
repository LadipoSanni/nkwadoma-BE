package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.*;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
@Slf4j
public class SecurityConfiguration {
    @Bean
    SecurityFilterChain resourceServerSecurityFilterChain(
            HttpSecurity http,
            Converter<Jwt, AbstractAuthenticationToken> authenticationConverter) throws Exception {
        http.oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwtDecoder -> jwtDecoder.jwtAuthenticationConverter(authenticationConverter)));

        http.sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).
                csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(requests -> {
            requests.requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll();
            requests.anyRequest().authenticated();
        });

        return http.build();
    }
}
