package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.BlackListedTokenAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final BlackListedTokenAdapter blackListedTokenAdapter;
    private final IdentityManagerOutputPort identityManagerOutputPort;
    private  final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Entering CustomAuthorizationFilter for request: {}", request.getRequestURI());
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            try {
                log.info("Finding user performing this action in the filter chain");
                UserIdentity userIdentity = identityManagerOutputPort.getUserById(jwt.getClaimAsString("sub"));
                log.info("Found user identity: {} and user is enabled {}", userIdentity.getEmail(), userIdentity.isEnabled());
                if (!userIdentity.isEnabled()){
                    log.warn("User is disabled and attempting to perform an action. User Email is: {} and user id is: {}", userIdentity.getEmail(), userIdentity.getId());
                    userIdentity.setAccessToken(jwt.getTokenValue());
                    identityManagerOutputPort.logout(userIdentity);
                    log.warn("Disabled user forcefully logged out");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(Map.of("Error", "Account deactivated!")));
                    return;
                }
            }catch (Exception e) {
                log.error("Error occurred in the filter chain trying to find user is enabled user {}",e.getMessage());
            }
            if (isLoggedOutOnSystem(jwt.getTokenValue())) {
                log.info("JWT token is blacklisted: {}", jwt.getTokenValue());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(Map.of("Error", "No More Session for this user")));
                return;
            }
        }
        log.info("Passing request down the filter chain for URI: {}", request.getRequestURI());
        filterChain.doFilter(request, response);
        log.info("Exiting CustomAuthorizationFilter for request: {}", request.getRequestURI());
    }

    private boolean isLoggedOutOnSystem(String accessToken) {
        log.info("check if token is black listed {}",blackListedTokenAdapter.isPresent(accessToken));
        return blackListedTokenAdapter.isPresent(accessToken);
    }
}
