package org.project.customerservice.dto;

import lombok.Builder;

@Builder
public record CreateCustomerRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {}