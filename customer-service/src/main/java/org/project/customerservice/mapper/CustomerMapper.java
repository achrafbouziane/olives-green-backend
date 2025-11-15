package org.project.customerservice.mapper;

import org.project.customerservice.dto.CustomerDTO;
import org.project.customerservice.dto.PropertyDTO;
import org.project.customerservice.entity.Customer;
import org.project.customerservice.entity.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Tells MapStruct to create a Spring Bean
public interface CustomerMapper {

    // MapStruct automatically maps fields with the same name.

    // We ignore the 'properties' list for this mapping, it's for fast list views
    @Mapping(target = "properties", ignore = true)
    CustomerDTO mapToCustomerDTO(Customer customer);

    // We must help MapStruct with the customerId
    @Mapping(target = "customerId", source = "customer.id")
    PropertyDTO mapToPropertyDTO(Property property);
}