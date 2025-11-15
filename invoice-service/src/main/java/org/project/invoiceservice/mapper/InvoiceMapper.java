package org.project.invoiceservice.mapper;


import org.project.invoiceservice.dto.InvoiceDTO;
import org.project.invoiceservice.dto.InvoiceLineItemDTO;
import org.project.invoiceservice.entity.Invoice;
import org.project.invoiceservice.entity.InvoiceLineItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    InvoiceDTO mapToInvoiceDTO(Invoice invoice);

    InvoiceLineItemDTO mapToInvoiceLineItemDTO(InvoiceLineItem lineItem);
}