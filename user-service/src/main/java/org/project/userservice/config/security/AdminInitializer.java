package org.project.userservice.config.security;

import org.project.userservice.domain.Role;
import org.project.userservice.entity.User;
import org.project.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            if (userRepository.findByEmail("admin@olivesgreen.com").isEmpty()) {
                User admin = User.builder()
                        .firstName("System")
                        .lastName("Admin")
                        .email("admin@olivesgreen.com")
                        .password(passwordEncoder.encode("admin123")) // Change this!
                        .role(Role.ADMIN)
                        .passwordChangeRequired(true)
                        .build();
                userRepository.save(admin);
                System.out.println("--- DEFAULT ADMIN USER CREATED ---");
                System.out.println("Email: admin@olivesgreen.com");
                System.out.println("Pass:  admin123");
            }
        };
    }
}