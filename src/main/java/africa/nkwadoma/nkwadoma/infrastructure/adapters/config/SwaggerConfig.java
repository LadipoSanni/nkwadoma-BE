package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

import java.util.*;

@Configuration
@Profile(value = {"local", "dev"})
public class SwaggerConfig {
    @Value("${springdoc.swagger-ui.env.url}")
    private String swaggerEnvPath;
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server();
        server.setUrl(swaggerEnvPath);

        return new OpenAPI().servers(List.of(server)).addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Meedl API Documentation")
//                        .description("Some custom description of API.")
                        .version("1.0")
                );
    }
}
