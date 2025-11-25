package org.project.customerservice.repository;

import org.project.customerservice.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
    // Find all properties for a specific customer
    List<Property> findByCustomerId(UUID customerId);
}