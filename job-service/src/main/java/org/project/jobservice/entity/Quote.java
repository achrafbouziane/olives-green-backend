package org.project.jobservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.jobservice.domain.QuoteStatus;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    @GeneratedValue
    private UUID id;

    // --- Links to other microservices ---
    @Column(nullable = false)
    private UUID customerId; // From customer-service

    @Column(nullable = false)
    private UUID propertyId; // From customer-service

    // --- Quote Details ---
    @Column(nullable = false)
    private String title; // e.g., "Spring Cleanup"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuoteStatus status;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private String magicLinkToken; // Unique token for email link
    private BigDecimal depositAmount; // The 50% required
    private Instant estimateSentAt;

    @Column(columnDefinition = "TEXT") // Allow long text for coords/address
    private String requestDetails;


    private Instant createdAt;
    private Instant sentAt;
    private Instant approvedAt;

    // A Quote has many LineItems
    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LineItem> lineItems = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "quote_mockups", joinColumns = @JoinColumn(name = "quote_id"))
    @Column(name = "image_url")
    private List<String> mockupImageUrls;

}