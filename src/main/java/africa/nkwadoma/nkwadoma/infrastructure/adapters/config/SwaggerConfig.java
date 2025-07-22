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

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Value("${server.port}")
    private String serverPort;

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .name("Authorization")
                .in(SecurityScheme.In.HEADER);
    }


    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(getServerList())
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info()
                        .title("Meedl API Documentation")
                        .version("1.0")
                        .description("API documentation for Nkwadoma application"));
    }

    private List<Server> getServerList() {
        return List.of(
                new Server()
                        .url((swaggerEnvPath != null ? swaggerEnvPath : "http://localhost:") + serverPort + contextPath)
                        .description("Environment Server"),
                new Server()
                        .url("http://localhost:" + serverPort + contextPath)
                        .description("Local Development"),
                new Server()
                        .url("https://api-systest.meedl.africa" + contextPath)
                        .description("Test Environment")
        );
    }

//    private List<Server> getServerList() {
//        Server server = new Server();
//        server.setUrl(swaggerEnvPath);
//        Server localServer1 = new Server();
//        localServer1.setUrl("http://localhost:8081");
//        Server localServer2 = new Server();
//        localServer2.setUrl("http://localhost:8080");
//        Server devServer = new Server();
//        devServer.setUrl("https://api-systest.meedl.africa");
//
//        return List.of(server, localServer1, localServer2, devServer);
//    }

}
