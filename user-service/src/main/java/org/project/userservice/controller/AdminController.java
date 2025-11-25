package org.project.userservice.controller;

import org.project.userservice.dto.RegisterRequest;
import org.project.userservice.dto.UpdateUserRequest;
import org.project.userservice.dto.UserDTO;
import org.project.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}