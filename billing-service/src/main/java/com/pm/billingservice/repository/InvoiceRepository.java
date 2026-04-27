package com.pm.billingservice.repository;

import com.pm.billingservice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {

    List<Invoice> findByBillingAccountId(String billingAccountId);
    // Filter by status
    List<Invoice> findByStatus(String status);

    // Filter by account and status
    List<Invoice> findByBillingAccountIdAndStatus(String billingAccountId, String status);

    // Find overdue invoices
    @Query("SELECT i FROM Invoice i WHERE i.status = 'PENDING' AND i.dueDate < :today")
    List<Invoice> findOverdueInvoices(@Param("today") LocalDate today);
}