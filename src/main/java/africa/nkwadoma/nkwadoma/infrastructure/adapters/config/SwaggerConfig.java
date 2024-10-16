//package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.*;
import org.springdoc.core.models.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

import java.util.*;

//@Configuration
//@Profile(value = {"default", "dev"})
//public class SwaggerConfig {
//    @Value("${springdoc.swagger-ui.path}")
//    private String swaggerUiPath;
//
//    @Bean
//    public OpenAPI apiInfo() {
//        Server server = new Server();
//        server.setUrl(swaggerUiPath);
//        return new OpenAPI()
//                .servers(List.of(server))
//                .info(new Info().title("Meedl API")
//                        .version("1.0.0")
//                       .description("API Documentation for Meedl")
//                );
//    }

//    @Bean
//    public GroupedOpenApi publicApi() {
//        return GroupedOpenApi.builder()
//                .group("public")
//                .pathsToMatch("/**")
//                .build();
//    }
//}
