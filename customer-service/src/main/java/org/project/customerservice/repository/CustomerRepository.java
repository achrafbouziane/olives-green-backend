package org.project.customerservice.repository;

import org.project.customerservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    // Optional<Customer> findByEmail(String email);
}