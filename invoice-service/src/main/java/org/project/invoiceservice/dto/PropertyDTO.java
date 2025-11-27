package org.project.invoiceservice.dto;

import java.util.UUID;

public record PropertyDTO(
        UUID id,
        String addressLine1,
        String city,
        String state,
        String postalCode
) {}