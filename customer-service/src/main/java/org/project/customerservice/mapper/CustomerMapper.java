package org.project.customerservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.project.customerservice.dto.*;
import org.project.customerservice.entity.Customer;
import org.project.customerservice.entity.Property;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // Entity to DTO
    CustomerDTO mapToCustomerDTO(Customer customer);
    PropertyDTO mapToPropertyDTO(Property property);

    // Request to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "properties", ignore = true)
    Customer toEntity(CreateCustomerRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
        // MapStruct maps 'state' automatically since names match
    Property toEntity(CreatePropertyRequest request);
}