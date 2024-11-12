package africa.nkwadoma.nkwadoma.infrastructure.adapters.config.allowedHosts;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Profile("dev")
@Component
public class DevTestPatterns implements AllowedHost{
    @Override
    public String[] getPatterns() {
        return new String[]{
                "http://localhost:3000",
                "http://localhost:3000/",
                "http://localhost:3000/**",
                "https://api-systest.learnspace.africa/",
                "http://api-systest.learnspace.africa",
                "http://api-systest.learnspace.africa/**",
                "http://api-systest.learnspace.africa/swagger-ui/**"
        };
    }
}
