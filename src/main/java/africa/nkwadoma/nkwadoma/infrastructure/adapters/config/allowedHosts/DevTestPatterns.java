package africa.nkwadoma.nkwadoma.infrastructure.adapters.config.allowedHosts;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Profile("dev")
@Component
public class DevTestPatterns implements AllowedHost{
    @Value("${dev}")
    private String allowedHost;
    @Value("${dev2}")
    private String allowedHost2;
    @Override
    public String[] getPatterns() {
        return new String[]{
                allowedHost,
                allowedHost2
        };
    }
}
