package org.project.invoiceservice.controller;

import org.project.invoiceservice.dto.PaymentIntentRequest;
import org.project.invoiceservice.dto.PaymentIntentResponse;
import org.project.invoiceservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-intent")
    public ResponseEntity<PaymentIntentResponse> createIntent(@RequestBody PaymentIntentRequest request) {
        try {
            return ResponseEntity.ok(paymentService.createPaymentIntent(request));
        } catch (Exception e) {
            // Handle error properly in production
            throw new RuntimeException("Stripe Error: " + e.getMessage());
        }
    }
}