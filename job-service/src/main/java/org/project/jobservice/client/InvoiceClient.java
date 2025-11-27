package org.project.jobservice.client;

import org.project.jobservice.dto.CreateInvoiceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "invoice-service", path = "/api/v1/invoices")
public interface InvoiceClient {

    @PostMapping
    void createInvoice(@RequestBody CreateInvoiceRequest request);
}

