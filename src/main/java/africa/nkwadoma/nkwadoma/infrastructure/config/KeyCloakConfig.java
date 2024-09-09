package africa.nkwadoma.nkwadoma.infrastructure.config;

import org.keycloak.admin.client.Keycloak;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class KeyCloakConfig {
    @Value("${keycloak.server.url}")
    private String KEYCLOAK_SERVER_URL;


    @Value("${keycloak.auth.user}")
    private String KEYCLOAK_USERNAME;
    @Value("${keycloak.auth.password}")
    private String KEYCLOAK_PASSWORD;

    @Bean
    @Primary
    public Keycloak keycloakConfigResolver() {
        return Keycloak.getInstance(
                KEYCLOAK_SERVER_URL,
                "master",
                KEYCLOAK_USERNAME,
                KEYCLOAK_PASSWORD,
                "admin-cli");
    }
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper;
    }
}
