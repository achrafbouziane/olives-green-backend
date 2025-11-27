package org.project.invoiceservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.invoiceservice.domain.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue
    private UUID id;

    // --- Links to other services ---
    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false, unique = true)
    private UUID jobId; // The job this invoice is for

    // --- Invoice Details ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime issuedDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    private LocalDateTime paidDate;

    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String serviceAddress; // The property address snapshot

    // An Invoice has many LineItems
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InvoiceLineItem> lineItems = new ArrayList<>();
}