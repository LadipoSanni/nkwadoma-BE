package africa.nkwadoma.nkwadoma.infrastructure.adapters.config.allowedHosts;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
public class LocalPatterns implements AllowedHost{
    @Value("${local}")
    private String localAllowedHost;
    @Override
    public String[] getPatterns() {
        return new String[]{
                localAllowedHost
        };
    }
}
