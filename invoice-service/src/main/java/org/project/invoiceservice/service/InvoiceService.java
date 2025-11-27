package org.project.invoiceservice.service;


import org.project.invoiceservice.dto.CreateInvoiceRequest;
import org.project.invoiceservice.dto.InvoiceDTO;

import java.util.List;
import java.util.UUID;

public interface InvoiceService {

    List<InvoiceDTO> getAllInvoices();

    /**
     * Creates a new invoice from a completed job.
     */
    InvoiceDTO createInvoice(CreateInvoiceRequest request);

    /**
     * Marks an invoice as PAID.
     */
    InvoiceDTO markInvoiceAsPaid(UUID invoiceId);

    /**
     * Marks an invoice as SENT.
     */
    InvoiceDTO markInvoiceAsSent(UUID invoiceId);

    /**
     * Gets a single invoice by its ID.
     */
    InvoiceDTO getInvoiceById(UUID invoiceId);

    /**
     * Gets all invoices for a specific customer.
     */
    List<InvoiceDTO> getInvoicesForCustomer(UUID customerId);
}