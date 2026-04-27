package com.pm.billingservice.controller;

import com.pm.billingservice.dto.InvoiceRequest;
import com.pm.billingservice.dto.InvoiceResponse;
import com.pm.billingservice.service.InvoiceService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")

public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(
            @RequestBody @Valid InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @GetMapping("/account/{billingAccountId}")
    public ResponseEntity<List<InvoiceResponse>> getByAccount(
            @PathVariable String billingAccountId) {
        return ResponseEntity.ok(invoiceService.getByAccountId(billingAccountId));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<InvoiceResponse> markAsPaid(@PathVariable String id) {
        return ResponseEntity.ok(invoiceService.markAsPaid(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<InvoiceResponse> cancel(@PathVariable String id) {
        return ResponseEntity.ok(invoiceService.cancelInvoice(id));
    }
}