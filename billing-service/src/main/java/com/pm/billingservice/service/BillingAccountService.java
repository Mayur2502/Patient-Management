package com.pm.billingservice.service;

import com.pm.billingservice.dto.BillingAccountRequest;
import com.pm.billingservice.dto.BillingAccountResponse;
import com.pm.billingservice.exception.DuplicateResourceException;
import com.pm.billingservice.exception.InvalidOperationException;
import com.pm.billingservice.exception.ResourceNotFoundException;
import com.pm.billingservice.model.BillingAccount;
import com.pm.billingservice.repository.BillingAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillingAccountService {

    private static final Logger log = LoggerFactory.getLogger(BillingAccountService.class);

    private final BillingAccountRepository billingAccountRepository;

    public BillingAccountService(BillingAccountRepository billingAccountRepository) {
        this.billingAccountRepository = billingAccountRepository;
    }

    // Create a new billing account (called when a patient is created)
    public BillingAccountResponse createAccount(BillingAccountRequest request) {

        if (billingAccountRepository.existsByPatientId(request.getPatientId())) {
            log.warn("Billing account already exists for patientId: {}",
                    request.getPatientId());
            throw new DuplicateResourceException(
                    "Billing account already exists for patient: "
                            + request.getPatientId());
        }

        BillingAccount account = new BillingAccount();
        account.setPatientId(request.getPatientId());
        account.setPatientName(request.getPatientName());
        account.setStatus("ACTIVE");

        BillingAccount saved = billingAccountRepository.save(account);
        log.info("Billing account created for patientId: {}", request.getPatientId());

        return mapToResponse(saved);
    }

    // Get billing account by patientId
    public BillingAccountResponse getByPatientId(String patientId) {

        BillingAccount account = billingAccountRepository
                .findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Billing account not found for patientId: " + patientId));

        return mapToResponse(account);
    }

    // Get billing account by account ID
    public BillingAccountResponse getById(String id) {

        BillingAccount account = billingAccountRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Billing account not found with id: " + id));

        return mapToResponse(account);
    }

    // Get all billing accounts
    public List<BillingAccountResponse> getAllAccounts() {

        return billingAccountRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Suspend a billing account
    public BillingAccountResponse suspendAccount(String id) {

        BillingAccount account = billingAccountRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Billing account not found with id: " + id));

        account.setStatus("SUSPENDED");
        BillingAccount saved = billingAccountRepository.save(account);
        log.info("Billing account suspended: {}", id);

        return mapToResponse(saved);
    }

    // Map entity to response DTO
    private BillingAccountResponse mapToResponse(BillingAccount account) {

        BillingAccountResponse response = new BillingAccountResponse();
        response.setId(account.getId());
        response.setPatientId(account.getPatientId());
        response.setPatientName(account.getPatientName());
        response.setStatus(account.getStatus());
        response.setCreatedAt(account.getCreatedAt());

        return response;
    }
    public BillingAccountResponse reactivateAccount(String id) {

        BillingAccount account = billingAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Billing account not found with id: " + id));

        if ("ACTIVE".equals(account.getStatus())) {
            throw new InvalidOperationException(
                    "Billing account is already active: " + id);
        }

        account.setStatus("ACTIVE");
        BillingAccount saved = billingAccountRepository.save(account);
        log.info("Billing account reactivated: {}", id);
        return mapToResponse(saved);
    }
}