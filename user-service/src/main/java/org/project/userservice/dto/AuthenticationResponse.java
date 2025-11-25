package org.project.userservice.dto;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String token,
        UserDTO user,
        boolean requiresPasswordChange
) {}