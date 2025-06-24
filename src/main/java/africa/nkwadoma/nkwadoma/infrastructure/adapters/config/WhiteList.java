package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import org.springframework.beans.factory.annotation.Value;


public class WhiteList {
//    @Value("${api.version}")
//    private static String api_version;
    public static final String[] patterns = {
            "/swagger-ui/index.html",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/v3/api-docs",
            "/webjars/**",
            "/actuator/health",
//            api_version+ "auth/login",
//            api_version+ "auth/password/create",
//            api_version+ "auth/password/forgotPassword",
            "/api/v1/auth/password/forgotPassword",
//            api_version+ "auth/refresh-token",
//            api_version+ "auth/password/reset",
//            api_version+ "identity/verification/failure-record/create",
//            api_version+ "investment-vehicle/detail/link/**"
    };
}
