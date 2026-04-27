package com.pm.billingservice.controller;

import com.pm.billingservice.dto.BillingAccountRequest;
import com.pm.billingservice.dto.BillingAccountResponse;
import com.pm.billingservice.service.BillingAccountService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing-accounts")

public class BillingAccountController {

    private final BillingAccountService service;

    public BillingAccountController(BillingAccountService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BillingAccountResponse> create(
            @RequestBody @Valid BillingAccountRequest request) {
        return ResponseEntity.ok(service.createAccount(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingAccountResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<BillingAccountResponse> getByPatientId(
            @PathVariable String patientId) {
        return ResponseEntity.ok(service.getByPatientId(patientId));
    }

    @GetMapping
    public ResponseEntity<List<BillingAccountResponse>> getAll() {
        return ResponseEntity.ok(service.getAllAccounts());
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<BillingAccountResponse> suspend(@PathVariable String id) {
        return ResponseEntity.ok(service.suspendAccount(id));
    }
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<BillingAccountResponse> reactivate(@PathVariable String id) {
        return ResponseEntity.ok(service.reactivateAccount(id));
    }
}