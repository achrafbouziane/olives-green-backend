package org.project.userservice.controller;

import org.project.userservice.dto.RegisterRequest;
import org.project.userservice.dto.UserDTO;
import org.project.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')") // Only Admins can call this
    public ResponseEntity<UserDTO> createUser(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(userService.createUser(request));
    }
}