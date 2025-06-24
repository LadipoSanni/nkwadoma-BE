package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import org.springframework.beans.factory.annotation.Value;


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
            "/auth/login",
            "/auth/password/create",
            "/auth/password/forgotPassword",
            "/auth/password/forgotPassword",
            "/auth/refresh-token",
            "/auth/password/reset",
            "/identity/verification/failure-record/create",
            "/investment-vehicle/detail/link/**"
    };
}
