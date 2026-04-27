package com.pm.billingservice.service;

import com.pm.billingservice.dto.InvoiceRequest;
import com.pm.billingservice.dto.InvoiceResponse;
import com.pm.billingservice.exception.InvalidOperationException;
import com.pm.billingservice.exception.ResourceNotFoundException;
import com.pm.billingservice.model.Invoice;
import com.pm.billingservice.repository.BillingAccountRepository;
import com.pm.billingservice.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    private final InvoiceRepository invoiceRepository;
    private final BillingAccountRepository billingAccountRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          BillingAccountRepository billingAccountRepository) {
        this.invoiceRepository = invoiceRepository;
        this.billingAccountRepository = billingAccountRepository;
    }

    // Create a new invoice
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {

        // Make sure billing account exists before creating invoice
        billingAccountRepository
                .findById(invoiceRequest.getBillingAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Billing account not found with id: "
                                + invoiceRequest.getBillingAccountId()));

        Invoice invoice = new Invoice();
        invoice.setBillingAccountId(invoiceRequest.getBillingAccountId());
        invoice.setDescription(invoiceRequest.getDescription());
        invoice.setAmount(invoiceRequest.getAmount());
        invoice.setDueDate(invoiceRequest.getDueDate());
        invoice.setStatus("PENDING");

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice created for billingAccountId: {}",
                invoiceRequest.getBillingAccountId());

        return mapToResponse(saved);
    }

    // Get single invoice by ID
    public InvoiceResponse getById(String id) {

        Invoice invoice = invoiceRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + id));

        return mapToResponse(invoice);
    }

    // Get all invoices for a billing account
    public List<InvoiceResponse> getByAccountId(String billingAccountId) {

        // Check billing account exists first
        billingAccountRepository
                .findById(billingAccountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Billing account not found with id: " + billingAccountId));

        return invoiceRepository
                .findByBillingAccountId(billingAccountId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Mark invoice as PAID
    public InvoiceResponse markAsPaid(String id) {

        Invoice invoice = invoiceRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + id));

        if ("PAID".equals(invoice.getStatus())) {
            throw new InvalidOperationException(
                    "Invoice is already paid: " + id);
        }

        if ("CANCELLED".equals(invoice.getStatus())) {
            throw new InvalidOperationException(
                    "Cannot pay a cancelled invoice: " + id);
        }

        invoice.setStatus("PAID");
        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice marked as PAID: {}", id);

        return mapToResponse(saved);
    }

    // Cancel an invoice
    public InvoiceResponse cancelInvoice(String id) {

        Invoice invoice = invoiceRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + id));

        if ("CANCELLED".equals(invoice.getStatus())) {
            throw new InvalidOperationException(
                    "Invoice is already cancelled: " + id);
        }

        if ("PAID".equals(invoice.getStatus())) {
            throw new InvalidOperationException(
                    "Cannot cancel an already paid invoice: " + id);
        }

        invoice.setStatus("CANCELLED");
        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice cancelled: {}", id);

        return mapToResponse(saved);
    }

    // Map entity to response DTO
    private InvoiceResponse mapToResponse(Invoice invoice) {

        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setBillingAccountId(invoice.getBillingAccountId());
        response.setDescription(invoice.getDescription());
        response.setAmount(invoice.getAmount());
        response.setStatus(invoice.getStatus());
        response.setDueDate(invoice.getDueDate());
        response.setCreatedAt(invoice.getCreatedAt());

        return response;
    }
}