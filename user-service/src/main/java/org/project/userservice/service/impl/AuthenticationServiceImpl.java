package org.project.userservice.service.impl;
import jakarta.persistence.EntityNotFoundException;
import org.project.userservice.config.security.JwtService;
import org.project.userservice.dto.*;
import org.project.userservice.entity.User;
import org.project.userservice.mapper.UserMapper;
import org.project.userservice.repository.UserRepository;
import org.project.userservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("User not found after auth"));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(userMapper.mapToUserDTO(user))
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


}