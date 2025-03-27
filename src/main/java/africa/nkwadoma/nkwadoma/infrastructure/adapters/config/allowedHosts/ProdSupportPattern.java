package africa.nkwadoma.nkwadoma.infrastructure.adapters.config.allowedHosts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("prodsupport")
@Component
public class ProdSupportPattern implements AllowedHost{
    @Value("${prodsupport}")
    private String allowedHost;
    @Value("${prodsupport2}")
    private String allowedHost2;
    @Override
    public String[] getPatterns() {
        return new String[]{
                allowedHost,
                allowedHost2
        };
    }
}
