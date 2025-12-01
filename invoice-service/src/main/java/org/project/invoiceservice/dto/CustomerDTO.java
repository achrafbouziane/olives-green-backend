package org.project.invoiceservice.dto;

import java.util.List;
import java.util.UUID;

public record CustomerDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        List<PropertyDTO> properties // ✅ Ensure this list exists
) {}