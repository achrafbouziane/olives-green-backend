package org.project.gatewayservice.filter;

import org.project.gatewayservice.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }

            try {
                // 1. Validate Token
                jwtUtil.validateToken(authHeader);

                // 2. Check Role (Fixed logic)
                if (config.isAdminOnly) {
                    List<String> roles = jwtUtil.extractRoles(authHeader);
                    // Check if "authorities" list contains "ADMIN"
                    if (roles == null || !roles.contains("ADMIN")) {
                        return onError(exchange, "Admin Access Required", HttpStatus.FORBIDDEN);
                    }
                }

            } catch (Exception e) {
                return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        public boolean isAdminOnly;

        // Add getters/setters to ensure Spring binds the 'args' from YAML correctly
        public boolean isAdminOnly() {
            return isAdminOnly;
        }

        public void setAdminOnly(boolean adminOnly) {
            isAdminOnly = adminOnly;
        }
    }
}