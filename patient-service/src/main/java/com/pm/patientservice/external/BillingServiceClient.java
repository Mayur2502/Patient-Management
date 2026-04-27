package com.pm.patientservice.external;

import com.pm.patientservice.exception.BillingServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class BillingServiceClient {

    private static final Logger log =
            LoggerFactory.getLogger(BillingServiceClient.class);

    private final RestTemplate restTemplate;

    @Value("${billing.service.url}")
    private String billingServiceUrl;

    public BillingServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createBillingAccount(String patientId, String patientName) {

        BillingAccountRequest request =
                new BillingAccountRequest(patientId, patientName);

        try {
            restTemplate.postForObject(
                    billingServiceUrl + "/api/billing-accounts",
                    request,
                    Void.class
            );
            log.info("Billing account created successfully for patientId: {}",
                    patientId);

        } catch (RestClientException e) {
            log.error("Billing service unreachable for patientId: {}. Reason: {}",
                    patientId, e.getMessage());
            throw new BillingServiceException(
                    "Billing service is currently unavailable. " +
                    "Patient was saved but billing account was not created " +
                    "for patientId: " + patientId, e);
        }
    }
}