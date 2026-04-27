package com.pm.billingservice.dto;

import jakarta.validation.constraints.NotBlank;

public class BillingAccountRequest {

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    @NotBlank(message = "Patient name is required")
    private String patientName;

    public BillingAccountRequest() {}

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
}