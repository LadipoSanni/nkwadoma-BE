package africa.nkwadoma.nkwadoma.infrastructure.adapters.config.allowedHosts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("systest")
@Component
public class SystestPatterns implements AllowedHost{
    @Value("${systest}")
    private String allowedHost;
    @Value("${systest}")
    private String allowedHost2;
    @Override
    public String[] getPatterns() {
        return new String[]{
                allowedHost,
                allowedHost2
        };
    }
}
