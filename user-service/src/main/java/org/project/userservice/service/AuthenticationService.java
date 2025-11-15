package org.project.userservice.service;

import org.project.userservice.dto.AuthenticationResponse;
import org.project.userservice.dto.LoginRequest;
import org.project.userservice.dto.UserDTO;

public interface AuthenticationService {
    AuthenticationResponse login(LoginRequest request);
    UserDTO validateToken(String token);
}
