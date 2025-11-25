package org.project.invoiceservice.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.project.invoiceservice.dto.PaymentIntentRequest;
import org.project.invoiceservice.dto.PaymentIntentResponse;
import org.project.invoiceservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request) throws StripeException {
        // 1. Calculate Fee (3%)
        BigDecimal baseAmount = request.amount();
        BigDecimal feePercentage = new BigDecimal("0.03");
        BigDecimal feeAmount = baseAmount.multiply(feePercentage);
        BigDecimal totalAmount = baseAmount.add(feeAmount);

        // 2. Convert to Cents (Stripe expects integers)
        long amountInCents = totalAmount.multiply(new BigDecimal(100)).longValue();

        // 3. Create Params
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(request.currency())
                .putMetadata("quoteId", request.quoteId().toString())
                .putMetadata("baseAmount", baseAmount.toString())
                .putMetadata("feeAmount", feeAmount.toString())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        // 4. Call Stripe
        PaymentIntent intent = PaymentIntent.create(params);

        return new PaymentIntentResponse(
                intent.getClientSecret(),
                intent.getId(),
                totalAmount,
                feeAmount
        );
    }
}