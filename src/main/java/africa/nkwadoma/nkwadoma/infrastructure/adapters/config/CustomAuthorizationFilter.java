package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

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
    private  final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Entering CustomAuthorizationFilter for request: {}", request.getRequestURI());
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
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
