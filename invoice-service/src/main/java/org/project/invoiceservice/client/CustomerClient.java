package org.project.invoiceservice.client;

import org.project.invoiceservice.dto.CustomerDTO;
import org.project.invoiceservice.dto.PropertyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-service", path = "/api/v1/customers")
public interface CustomerClient {

    @GetMapping("/{id}")
    CustomerDTO getCustomerById(@PathVariable("id") UUID id);

    // ✅ ADD THIS: Fetch property directly (assuming endpoint exists)
    // If this endpoint doesn't exist in Customer Service, we will find it via CustomerDTO below.
    @GetMapping("/properties/{id}")
    PropertyDTO getPropertyById(@PathVariable("id") UUID id);
}