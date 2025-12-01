package org.project.userservice.dto;

import lombok.Builder;
import org.project.userservice.domain.Role;

@Builder
public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String password,
        Role role
) {}