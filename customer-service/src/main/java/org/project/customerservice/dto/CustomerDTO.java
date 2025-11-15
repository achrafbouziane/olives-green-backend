package org.project.customerservice.dto;

import lombok.Builder;
import java.util.List;
import java.util.UUID;

@Builder
public record CustomerDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        List<PropertyDTO> properties // We can include properties
) {
    public CustomerDTO withProperties(List<PropertyDTO> newProperties) {
        return new CustomerDTO(
                this.id,
                this.firstName,
                this.lastName,
                this.email,
                this.phoneNumber,
                newProperties // Use the new list
        );
    }
}