package com.medipatient.billing.repository;

import com.medipatient.billing.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByPatientId(UUID patientId);

    Page<Invoice> findByPatientId(UUID patientId, Pageable pageable);

    List<Invoice> findByAppointmentId(UUID appointmentId);

    List<Invoice> findByStatus(Invoice.Status status);

    Page<Invoice> findByStatus(Invoice.Status status, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE " +
           "i.dueDate < :today AND " +
           "i.status NOT IN ('PAID', 'CANCELLED')")
    List<Invoice> findOverdueInvoices(@Param("today") LocalDate today);

    @Query("SELECT i FROM Invoice i WHERE " +
           "i.dueDate < :today AND " +
           "i.status NOT IN ('PAID', 'CANCELLED')")
    Page<Invoice> findOverdueInvoices(@Param("today") LocalDate today, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE " +
           "i.dueDate BETWEEN :startDate AND :endDate AND " +
           "i.status NOT IN ('PAID', 'CANCELLED')")
    List<Invoice> findInvoicesDueBetween(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM Invoice i WHERE " +
           "(:patientId IS NULL OR i.patient.id = :patientId) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:dateFrom IS NULL OR i.createdAt >= CAST(:dateFrom AS timestamp)) AND " +
           "(:dateTo IS NULL OR i.createdAt <= CAST(:dateTo AS timestamp)) AND " +
           "(:amountMin IS NULL OR i.amount >= :amountMin) AND " +
           "(:amountMax IS NULL OR i.amount <= :amountMax)")
    Page<Invoice> findWithFilters(@Param("patientId") UUID patientId,
                                  @Param("status") Invoice.Status status,
                                  @Param("dateFrom") LocalDate dateFrom,
                                  @Param("dateTo") LocalDate dateTo,
                                  @Param("amountMin") Integer amountMin,
                                  @Param("amountMax") Integer amountMax,
                                  Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE " +
           "LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Invoice> searchByInvoiceNumber(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = :status")
    long countByStatus(@Param("status") Invoice.Status status);

    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE i.status = 'PAID'")
    Long getTotalPaidAmount();

    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE i.status = 'PAID' AND " +
           "i.createdAt >= CAST(:startDate AS timestamp) AND " +
           "i.createdAt <= CAST(:endDate AS timestamp)")
    Long getTotalPaidAmountBetween(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE " +
           "i.status NOT IN ('PAID', 'CANCELLED')")
    Long getTotalOutstandingAmount();

    @Query("SELECT i.status, COUNT(i) FROM Invoice i GROUP BY i.status")
    List<Object[]> countByStatusGrouped();

    @Query("SELECT DATE(i.createdAt), COUNT(i), SUM(i.amount) FROM Invoice i " +
           "WHERE i.createdAt >= CAST(:startDate AS timestamp) " +
           "GROUP BY DATE(i.createdAt) " +
           "ORDER BY DATE(i.createdAt)")
    List<Object[]> getInvoiceStatisticsByDate(@Param("startDate") LocalDate startDate);

    @Query("SELECT AVG(i.amount) FROM Invoice i WHERE i.status = 'PAID'")
    Double getAverageInvoiceAmount();

    @Query("SELECT i FROM Invoice i WHERE " +
           "DATE(i.createdAt) = :today")
    List<Invoice> findTodaysInvoices(@Param("today") LocalDate today);
}