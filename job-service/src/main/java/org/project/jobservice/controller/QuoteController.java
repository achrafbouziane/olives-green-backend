package org.project.jobservice.controller;

import org.project.jobservice.dto.CreateQuoteRequest;
import org.project.jobservice.dto.QuoteDTO;
import org.project.jobservice.dto.UpdateQuoteStatusRequest; // ✅ Import this
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

    @PostMapping("/{id}/send")
    public ResponseEntity<QuoteDTO> sendEstimate(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteService.sendEstimateToCustomer(id));
    }

    @PostMapping("/{id}/approve-estimate")
    public ResponseEntity<QuoteDTO> approveEstimate(@PathVariable UUID id, @RequestParam String token) {
        return ResponseEntity.ok(quoteService.customerApprove(id, token));
    }

    @PostMapping("/{id}/pay-deposit")
    public ResponseEntity<QuoteDTO> payDeposit(
            @PathVariable UUID id,
            @RequestParam String token,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(quoteService.payDeposit(id, token, amount));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<QuoteDTO> rejectQuote(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteService.rejectQuote(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<QuoteDTO> approveQuote(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteService.approveQuote(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuoteDTO> updateQuote(@PathVariable UUID id, @RequestBody CreateQuoteRequest request) {
        return ResponseEntity.ok(quoteService.updateQuote(id, request));
    }

    // ✅ ADD THIS METHOD
    @PutMapping("/{id}/status")
    public ResponseEntity<QuoteDTO> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateQuoteStatusRequest request
    ) {
        return ResponseEntity.ok(quoteService.updateQuoteStatus(id, request.status()));
    }
}