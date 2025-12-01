package org.project.userservice.service;

import org.project.userservice.dto.AuthenticationResponse;
import org.project.userservice.dto.LoginRequest;
import org.project.userservice.dto.UserDTO;
import org.project.userservice.entity.User;

public interface AuthenticationService {
    AuthenticationResponse login(LoginRequest request);
    UserDTO validateToken(String token);
    void changePassword(String email, String newPassword);
    AuthenticationResponse refreshToken(String refreshToken);

}
