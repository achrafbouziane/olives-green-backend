package org.project.invoiceservice.controller;


import org.project.invoiceservice.dto.CreateInvoiceRequest;
import org.project.invoiceservice.dto.InvoiceDTO;
import org.project.invoiceservice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    /**
     * This endpoint would be called by your job-service
     * when a job is marked 'COMPLETED'.
     */

    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody CreateInvoiceRequest request) {
        InvoiceDTO invoice = invoiceService.createInvoice(request);
        return new ResponseEntity<>(invoice, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesForCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(invoiceService.getInvoicesForCustomer(customerId));
    }

    /**
     * Marks an invoice as SENT.
     * This would be called by your admin panel.
     */
    @PostMapping("/{id}/send")
    public ResponseEntity<InvoiceDTO> sendInvoice(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.markInvoiceAsSent(id));
    }

    /**
     * Marks an invoice as PAID.
     * This could be called by your admin panel (for manual payment)
     * or by a payment gateway webhook.
     */
    @PostMapping("/{id}/pay")
    public ResponseEntity<InvoiceDTO> markAsPaid(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.markInvoiceAsPaid(id));
    }
}