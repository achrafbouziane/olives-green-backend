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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PropertyRepository propertyRepository;
    private final CustomerMapper customerMapper; // Injects the MapStruct bean

    @Override
    public CustomerDTO createCustomer(CreateCustomerRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        // Use the mapper to convert Entity -> DTO
        return customerMapper.mapToCustomerDTO(savedCustomer);
    }

    @Override
    public PropertyDTO addPropertyToCustomer(CreatePropertyRequest request) {
        // 1. Find the customer this property belongs to
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + request.customerId()));

        // 2. Build the new property
        Property property = Property.builder()
                .addressLine1(request.addressLine1())
                .addressLine2(request.addressLine2())
                .city(request.city())
                .postalCode(request.postalCode())
                .notes(request.notes())
                .customer(customer) // Link it to the customer
                .build();

        // 3. Save the property
        Property savedProperty = propertyRepository.save(property);

        // Use the mapper to convert Entity -> DTO
        return customerMapper.mapToPropertyDTO(savedProperty);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        // Use the mapper in a stream
        return customerRepository.findAll().stream()
                .map(customerMapper::mapToCustomerDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO getCustomerById(UUID id) {
        // This is an orchestrated call: get customer + get properties

        // 1. Find the customer
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + id));

        // 2. Find their properties
        List<Property> properties = propertyRepository.findByCustomerId(id);

        // 3. Map the customer (this DTO will have an empty 'properties' list)
        CustomerDTO customerDTO = customerMapper.mapToCustomerDTO(customer);

        // 4. Map the properties
        List<PropertyDTO> propertyDTOs = properties.stream()
                .map(customerMapper::mapToPropertyDTO)
                .collect(Collectors.toList());

        // 5. Return a new DTO with the property list included
        // (This uses the 'withProperties' method we added to the CustomerDTO record)
        return customerDTO.withProperties(propertyDTOs);
    }

    @Override
    public List<PropertyDTO> getPropertiesForCustomer(UUID customerId) {
        // Verify customer exists before fetching properties (optional good practice)
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("Customer not found with ID: " + customerId);
        }

        // Use the mapper in a stream
        return propertyRepository.findByCustomerId(customerId).stream()
                .map(customerMapper::mapToPropertyDTO)
                .collect(Collectors.toList());
    }
}