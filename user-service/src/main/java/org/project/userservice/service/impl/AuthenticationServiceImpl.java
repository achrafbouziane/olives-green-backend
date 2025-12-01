package org.project.userservice.service.impl;
import jakarta.persistence.EntityNotFoundException;
import org.project.userservice.config.security.JwtService;
import org.project.userservice.dto.*;
import org.project.userservice.entity.RefreshToken;
import org.project.userservice.entity.User;
import org.project.userservice.mapper.UserMapper;
import org.project.userservice.repository.RefreshTokenRepository;
import org.project.userservice.repository.UserRepository;
import org.project.userservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.nio.charset.Charset;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var user = userRepository.findByEmail(request.email()).orElseThrow();

        // 1. Generate Tokens
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user); // ensure JwtService has this method

        // 2. Save Refresh Token to DB
        revokeAllUserTokens(user); // Invalidate old tokens (optional but safer)
        saveUserToken(user, refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(user)
                .build();
    }
    @Override
    public UserDTO validateToken(String token) {
        // 1. Extract the user's email from the token
        final String userEmail = jwtService.extractUsername(token);
        if (userEmail == null) {
            throw new IllegalArgumentException("Invalid token");
        }

        // 2. Load the user from the database
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

        // 3. Validate the token against the user
        if (jwtService.isTokenValid(token, userDetails)) {
            // Token is valid, return the user's details
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            return userMapper.mapToUserDTO(user);
        }

        throw new IllegalArgumentException("Invalid token");
    }

    public void changePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(newPassword);
        user.setPasswordChangeRequired(false); // FLIP THE FLAG
        userRepository.save(user);
    }

    @Override
    public AuthenticationResponse refreshToken(String refreshToken) {
        // 1. Extract User
        String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail).orElseThrow();

            // 2. Check DB validity
            var isTokenValid = refreshTokenRepository.findByToken(refreshToken)
                    .map(t -> !t.expired && !t.revoked)
                    .orElse(false);

            // 3. Generate New Access Token if valid
            if (jwtService.isTokenValid(refreshToken, user) && isTokenValid) {
                var accessToken = jwtService.generateToken(user);
                // Note: We usually rotate the Refresh Token here too for max security,
                // but for simplicity, we'll just return a new Access Token.

                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken) // Return same or new one
                        .build();
            }
        }
        throw new RuntimeException("Invalid Refresh Token");
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = RefreshToken.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        refreshTokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = refreshTokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        refreshTokenRepository.saveAll(validUserTokens);
    }


}