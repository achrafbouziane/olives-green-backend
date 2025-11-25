package org.project.userservice.dto;

import org.project.userservice.domain.Role;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email,
        Role role
) {}