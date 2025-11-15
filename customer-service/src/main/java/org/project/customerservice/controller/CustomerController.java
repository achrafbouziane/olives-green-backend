package org.project.customerservice.controller;


import org.project.customerservice.dto.*;
import org.project.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers") // Base path for all customer-related APIs
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // --- Customer Endpoints ---

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CreateCustomerRequest request) {
        CustomerDTO newCustomer = customerService.createCustomer(request);
        return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    // --- Property Endpoints (Scoped to a customer) ---

    @PostMapping("/properties")
    public ResponseEntity<PropertyDTO> addProperty(@RequestBody CreatePropertyRequest request) {
        PropertyDTO newProperty = customerService.addPropertyToCustomer(request);
        return new ResponseEntity<>(newProperty, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/properties")
    public ResponseEntity<List<PropertyDTO>> getPropertiesForCustomer(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getPropertiesForCustomer(id));
    }
}