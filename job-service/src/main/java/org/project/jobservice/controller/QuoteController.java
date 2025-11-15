package org.project.jobservice.controller;

import org.project.jobservice.dto.CreateQuoteRequest;
import org.project.jobservice.dto.QuoteDTO;
import org.project.jobservice.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<QuoteDTO> getQuoteById(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteService.getQuoteById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<QuoteDTO>> getQuotesByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(quoteService.getQuotesForCustomer(customerId));
    }

    // This is how you approve a quote and convert it to a Job
    @PostMapping("/{id}/approve")
    public ResponseEntity<QuoteDTO> approveQuote(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteService.approveQuote(id));
    }
}