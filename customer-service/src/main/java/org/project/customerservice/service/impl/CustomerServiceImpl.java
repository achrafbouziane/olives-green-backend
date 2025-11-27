package org.project.customerservice.service.impl;

import org.project.customerservice.dto.*;
import org.project.customerservice.mapper.CustomerMapper;
import org.project.customerservice.entity.Customer;
import org.project.customerservice.entity.Property;
import org.project.customerservice.repository.CustomerRepository;
import org.project.customerservice.repository.PropertyRepository;
import org.project.customerservice.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PropertyRepository propertyRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerDTO createCustomer(CreateCustomerRequest request) {
        Customer customer = customerMapper.toEntity(request);

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.mapToCustomerDTO(savedCustomer);
    }

    @Override
    @Transactional
    public PropertyDTO addPropertyToCustomer(CreatePropertyRequest request) {
        // 1. Find the customer first
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + request.customerId()));

        // 2. AUTOMATIC: Convert Request -> Entity using Mapper
        Property property = customerMapper.toEntity(request);

        // 3. Link the relationship manually (MapStruct can't guess the parent object)
        property.setCustomer(customer);

        Property savedProperty = propertyRepository.save(property);
        return customerMapper.mapToPropertyDTO(savedProperty);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::mapToCustomerDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + id));

        List<Property> properties = propertyRepository.findByCustomerId(id);

        CustomerDTO customerDTO = customerMapper.mapToCustomerDTO(customer);
        List<PropertyDTO> propertyDTOs = properties.stream()
                .map(customerMapper::mapToPropertyDTO)
                .collect(Collectors.toList());

        return customerDTO.withProperties(propertyDTOs);
    }

    @Override
    public List<PropertyDTO> getPropertiesForCustomer(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("Customer not found with ID: " + customerId);
        }
        return propertyRepository.findByCustomerId(customerId).stream()
                .map(customerMapper::mapToPropertyDTO)
                .collect(Collectors.toList());
    }

    // --- Required for Duplicate Check in Quote Flow ---
    @Override
    public CustomerDTO getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with email: " + email));
        return customerMapper.mapToCustomerDTO(customer);
    }

    @Override
    public CustomerDTO updateCustomer(UUID id, UpdateCustomerRequest req) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (req.firstName() != null && !req.firstName().isBlank()) {
            customer.setFirstName(req.firstName());
        }

        if (req.lastName() != null && !req.lastName().isBlank()) {
            customer.setLastName(req.lastName());
        }

        if (req.email() != null && !req.email().isBlank()) {
            customer.setEmail(req.email());
        }

        if (req.phoneNumber() != null && !req.phoneNumber().isBlank()) {
            customer.setPhoneNumber(req.phoneNumber());
        }

        return customerMapper.mapToCustomerDTO(customerRepository.save(customer));
    }

}