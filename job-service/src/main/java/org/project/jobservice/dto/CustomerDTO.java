package org.project.jobservice.dto;

import lombok.Builder;
import java.util.UUID;

@Builder
public record CustomerDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {}