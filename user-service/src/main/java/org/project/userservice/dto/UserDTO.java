package org.project.userservice.dto;

import lombok.Builder;
import org.project.userservice.domain.Role;

@Builder
public record UserDTO(
        String id,
        String email,
        String firstName,
        String lastName,
        Role role
) {}