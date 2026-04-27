package com.pm.billingservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InvoiceResponse {

    private String id;
    private String billingAccountId;
    private String description;
    private BigDecimal amount;
    private String status;
    private LocalDate dueDate;
    private LocalDateTime createdAt;

    public InvoiceResponse() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBillingAccountId() { return billingAccountId; }
    public void setBillingAccountId(String billingAccountId) {
        this.billingAccountId = billingAccountId;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}