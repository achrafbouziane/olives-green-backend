package org.project.userservice.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // For method-level security (e.g., @PreAuthorize("hasRole('ADMIN')"))
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (Cross-Site Request Forgery) - Not needed for stateless APIs
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Define which API paths are public and which are secured
                .authorizeHttpRequests(auth -> auth
                        // Whitelist your public authentication endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Whitelist Eureka health check (if service registry is secured)
                        .requestMatchers("/actuator/health").permitAll()

                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )

                // 3. Configure session management to be STATELESS (we use JWTs)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Tell Spring to use our custom AuthenticationProvider
                .authenticationProvider(authenticationProvider)

                // 5. Add our custom JWT filter *before* the standard username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}