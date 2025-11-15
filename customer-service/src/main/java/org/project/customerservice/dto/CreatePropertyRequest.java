package org.project.customerservice.dto;

import lombok.Builder;
import java.util.UUID;

@Builder
public record CreatePropertyRequest(
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String notes,
        UUID customerId // The ID of the customer this property belongs to
) {}