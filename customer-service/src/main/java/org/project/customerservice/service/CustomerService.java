package org.project.customerservice.service;

import org.project.customerservice.dto.*;
import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerDTO createCustomer(CreateCustomerRequest request);
    PropertyDTO addPropertyToCustomer(CreatePropertyRequest request);
    List<CustomerDTO> getAllCustomers();
    CustomerDTO getCustomerById(UUID id);
    List<PropertyDTO> getPropertiesForCustomer(UUID customerId);

}