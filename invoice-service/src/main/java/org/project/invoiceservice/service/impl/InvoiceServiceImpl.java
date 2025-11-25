package org.project.invoiceservice.service.impl;


import org.project.invoiceservice.dto.CreateInvoiceRequest;
import org.project.invoiceservice.dto.InvoiceDTO;
import org.project.invoiceservice.mapper.InvoiceMapper;
import org.project.invoiceservice.entity.Invoice;
import org.project.invoiceservice.entity.InvoiceLineItem;
import org.project.invoiceservice.domain.InvoiceStatus;
import org.project.invoiceservice.repository.InvoiceRepository;
import org.project.invoiceservice.service.InvoiceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    @Transactional
    public InvoiceDTO createInvoice(CreateInvoiceRequest request) {

        Invoice invoice = Invoice.builder()
                .customerId(request.customerId())
                .jobId(request.jobId())
                .status(InvoiceStatus.DRAFT)
                .issuedDate(Instant.now())
                .dueDate(LocalDate.now().plusDays(30)) // Due in 30 days
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (var itemRequest : request.lineItems()) {
            BigDecimal lineTotal = itemRequest.unitPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));

            InvoiceLineItem lineItem = InvoiceLineItem.builder()
                    .description(itemRequest.description())
                    .unitPrice(itemRequest.unitPrice())
                    .quantity(itemRequest.quantity())
                    .total(lineTotal)
                    .invoice(invoice) // Link back to the parent
                    .build();

            invoice.getLineItems().add(lineItem);
            total = total.add(lineTotal);
        }

        invoice.setTotalAmount(total);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.mapToInvoiceDTO(savedInvoice);
    }

    @Override
    @Transactional
    public InvoiceDTO markInvoiceAsPaid(UUID invoiceId) {
        Invoice invoice = findInvoiceById(invoiceId);
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidDate(Instant.now());
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.mapToInvoiceDTO(savedInvoice);
    }

    @Override
    @Transactional
    public InvoiceDTO markInvoiceAsSent(UUID invoiceId) {
        Invoice invoice = findInvoiceById(invoiceId);
        // You can only send a Draft invoice
        if (invoice.getStatus() == InvoiceStatus.DRAFT) {
            invoice.setStatus(InvoiceStatus.SENT);

            // Here you would call your notification-service
            // notificationService.sendInvoiceEmail(invoice);

            Invoice savedInvoice = invoiceRepository.save(invoice);
            return invoiceMapper.mapToInvoiceDTO(savedInvoice);
        } else {
            throw new IllegalStateException("Invoice is not in DRAFT status, cannot send.");
        }
    }

    @Override
    public InvoiceDTO getInvoiceById(UUID invoiceId) {
        return invoiceMapper.mapToInvoiceDTO(findInvoiceById(invoiceId));
    }

    @Override
    public List<InvoiceDTO> getInvoicesForCustomer(UUID customerId) {
        return invoiceRepository.findByCustomerId(customerId).stream()
                .map(invoiceMapper::mapToInvoiceDTO)
                .collect(Collectors.toList());
    }

    // --- Private Helper ---
    private Invoice findInvoiceById(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with ID: " + invoiceId));
    }
}
