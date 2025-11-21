package org.project.jobservice.controller;

import org.project.jobservice.dto.CreateQuoteRequest;
import org.project.jobservice.dto.QuoteDTO;
import org.project.jobservice.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    // --- Standard CRUD ---

    @PostMapping
    public ResponseEntity<QuoteDTO> createQuote(@RequestBody CreateQuoteRequest request) {
        return new ResponseEntity<>(quoteService.createQuote(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<QuoteDTO>> getAllQuotes() {
        return ResponseEntity.ok(quoteService.getAllQuotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteDTO> getQuoteById(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteService.getQuoteById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<QuoteDTO>> getQuotesByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(quoteService.getQuotesForCustomer(customerId));
    }

    // --- Estimate & Workflow Endpoints ---

    // 1. Admin sends estimate to customer (Triggered by Admin Panel)
    @PostMapping("/{id}/send")
    public ResponseEntity<QuoteDTO> sendEstimate(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteService.sendEstimateToCustomer(id));
    }

    // 2. Customer approves via Magic Link
    @PostMapping("/{id}/approve-estimate")
    public ResponseEntity<QuoteDTO> approveEstimate(@PathVariable UUID id, @RequestParam String token) {
        return ResponseEntity.ok(quoteService.customerApprove(id, token));
    }

    // 3. Customer pays deposit (50%)
    @PostMapping("/{id}/pay-deposit")
    public ResponseEntity<QuoteDTO> payDeposit(
            @PathVariable UUID id,
            @RequestParam String token,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(quoteService.payDeposit(id, token, amount));
    }

    // 4. Customer or Admin rejects the quote
    @PostMapping("/{id}/reject")
    public ResponseEntity<QuoteDTO> rejectQuote(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteService.rejectQuote(id));
    }

    // 5. Admin Force Approve (Optional override)
    @PostMapping("/{id}/approve")
    public ResponseEntity<QuoteDTO> approveQuote(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteService.approveQuote(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuoteDTO> updateQuote(@PathVariable UUID id, @RequestBody CreateQuoteRequest request) {
        return ResponseEntity.ok(quoteService.updateQuote(id, request));
    }
}