package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

public class WhiteList {
    public static final String[] patterns = {
            "/swagger-ui/index.html",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/v3/api-docs",
            "/webjars/**",
            "/actuator/health",
            "/api/v1/auth/login",
            "/api/v1/auth/password/create",
            "/api/v1/auth/password/forgotPassword"
    };
}
