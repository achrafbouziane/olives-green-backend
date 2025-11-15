package org.project.invoiceservice.repository;

import org.project.invoiceservice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByCustomerId(UUID customerId);
    List<Invoice> findByStatus(org.project.invoiceservice.domain.InvoiceStatus status);
}