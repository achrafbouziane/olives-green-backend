package org.project.invoiceservice.service.impl;

import org.project.invoiceservice.client.CustomerClient;
import org.project.invoiceservice.client.NotificationClient;
import org.project.invoiceservice.domain.NotificationChannel;
import org.project.invoiceservice.dto.*;
import org.project.invoiceservice.mapper.InvoiceMapper;
import org.project.invoiceservice.entity.Invoice;
import org.project.invoiceservice.entity.InvoiceLineItem;
import org.project.invoiceservice.domain.InvoiceStatus;
import org.project.invoiceservice.repository.InvoiceRepository;
import org.project.invoiceservice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final NotificationClient notificationClient;
    private final CustomerClient customerClient;


    @Override
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll(Sort.by(Sort.Direction.DESC, "issuedDate"))
                .stream()
                .map(invoiceMapper::mapToInvoiceDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvoiceDTO createInvoice(CreateInvoiceRequest request) {
        Invoice invoice = Invoice.builder()
                .customerId(request.customerId())
                .jobId(request.jobId())
                .customerName(request.customerName()) // Save Snapshot
                .customerEmail(request.customerEmail())
                .customerPhone(request.customerPhone())
                .serviceAddress(request.serviceAddress())
                .status(InvoiceStatus.DRAFT)
                .issuedDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(30))
                .lineItems(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        if (request.lineItems() != null) {
            for (var itemRequest : request.lineItems()) {
                BigDecimal lineTotal = itemRequest.unitPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
                InvoiceLineItem lineItem = InvoiceLineItem.builder()
                        .description(itemRequest.description())
                        .unitPrice(itemRequest.unitPrice())
                        .quantity(itemRequest.quantity())
                        .total(lineTotal)
                        .invoice(invoice)
                        .build();
                invoice.getLineItems().add(lineItem);
                total = total.add(lineTotal);
            }
        }
        invoice.setTotalAmount(total);
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Send Notification
        sendInvoiceNotification(savedInvoice, request.type());

        return invoiceMapper.mapToInvoiceDTO(savedInvoice);
    }

    private void sendInvoiceNotification(Invoice invoice, String type) {
        try {
            // Use Snapshot Email if available, else fallback to Client
            String emailTo = invoice.getCustomerEmail();
            String nameTo = invoice.getCustomerName();

            if (emailTo == null) {
                CustomerDTO customer = customerClient.getCustomerById(invoice.getCustomerId());
                emailTo = customer.email();
                nameTo = customer.firstName();
            }

            String templateKey = "invoice-created";
            if ("FINAL".equalsIgnoreCase(type)) templateKey = "final-invoice-ready";
            else if ("DEPOSIT".equalsIgnoreCase(type)) templateKey = "deposit-receipt";

            Map<String, Object> params = new HashMap<>();
            params.put("customerName", nameTo);
            params.put("invoiceId", invoice.getId().toString());
            params.put("amount", invoice.getTotalAmount());
            params.put("dueDate", invoice.getDueDate().toString());
            params.put("link", "http://localhost:4200/invoices/" + invoice.getId());

            NotificationRequest email = new NotificationRequest();
            email.setChannel(NotificationChannel.EMAIL);
            email.setRecipient(emailTo);
            email.setTemplateKey(templateKey);
            email.setParameters(params);
            notificationClient.sendNotification(email);

        } catch (Exception e) {
            System.err.println("Failed to send invoice notification: " + e.getMessage());
        }
    }

    // ... (markInvoiceAsPaid, markInvoiceAsSent, getInvoiceById, etc. - KEEP AS IS) ...
    @Override
    public InvoiceDTO markInvoiceAsPaid(UUID id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidDate(LocalDateTime.now());
        return invoiceMapper.mapToInvoiceDTO(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceDTO markInvoiceAsSent(UUID id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        if (invoice.getStatus() == InvoiceStatus.DRAFT) invoice.setStatus(InvoiceStatus.SENT);
        return invoiceMapper.mapToInvoiceDTO(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceDTO getInvoiceById(UUID id) { return invoiceMapper.mapToInvoiceDTO(invoiceRepository.findById(id).orElseThrow()); }

    @Override
    public List<InvoiceDTO> getInvoicesForCustomer(UUID id) { return invoiceRepository.findByCustomerId(id).stream().map(invoiceMapper::mapToInvoiceDTO).collect(Collectors.toList()); }
}