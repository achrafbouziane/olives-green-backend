package org.project.invoiceservice.service;

import com.stripe.exception.StripeException;
import org.project.invoiceservice.dto.PaymentIntentRequest;
import org.project.invoiceservice.dto.PaymentIntentResponse;

public interface PaymentService {
    PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request) throws StripeException;
}
