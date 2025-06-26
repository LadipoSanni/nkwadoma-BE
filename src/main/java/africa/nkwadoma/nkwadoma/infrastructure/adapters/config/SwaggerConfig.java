package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

import java.util.*;

@Configuration
@Profile(value = {"local", "dev", "systest", "uat"})
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
        return new OpenAPI().servers(getServerList()).addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Meedl API Documentation")
                        .version("1.0")
                );
    }
    private List<Server> getServerList() {
        Server server = new Server();
        server.setUrl(swaggerEnvPath);
        Server localServer1 = new Server();
        localServer1.setUrl("http://localhost:8081");
        Server localServer2 = new Server();
        localServer2.setUrl("http://localhost:8080");
        Server devServer = new Server();
        devServer.setUrl("https://api-systest.meedl.africa");

        return List.of(server, localServer1, localServer2, devServer);
    }
}
