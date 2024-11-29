package africa.nkwadoma.nkwadoma.infrastructure.adapters.config.allowedHosts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("uat")
@Component
public class UatPatterns implements AllowedHost{
    @Value("${uat}")
    private String allowedHost;
    @Override
    public String[] getPatterns() {
        return new String[]{
                allowedHost
        };
    }
}
