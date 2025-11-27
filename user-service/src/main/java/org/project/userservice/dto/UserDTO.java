package org.project.userservice.dto;

import lombok.Builder;
import org.project.userservice.domain.Role;
import java.util.UUID;

@Builder
public record UserDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Role role
) {}