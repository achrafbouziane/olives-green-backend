package org.project.customerservice.dto;

public record UpdateCustomerRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {}
