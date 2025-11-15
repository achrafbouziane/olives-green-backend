package org.project.customerservice.dto;

import lombok.Builder;
import java.util.UUID;

@Builder
public record PropertyDTO(
        UUID id,
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String notes,
        UUID customerId
) {}