package org.project.userservice.controller;

import org.project.userservice.dto.AuthenticationResponse;
import org.project.userservice.dto.ChangePasswordRequest;
import org.project.userservice.dto.LoginRequest;
import org.project.userservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal principal) {

        authService.changePassword(principal.getName(), request.newPassword());
        return ResponseEntity.ok().build();
    }


}