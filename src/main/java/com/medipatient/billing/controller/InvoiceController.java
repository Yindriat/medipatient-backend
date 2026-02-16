package com.medipatient.billing.controller;

import com.medipatient.billing.dto.CreateInvoiceDto;
import com.medipatient.billing.dto.InvoiceDto;
import com.medipatient.billing.dto.UpdateInvoiceDto;
import com.medipatient.billing.model.Invoice;
import com.medipatient.billing.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Facturation", description = "Gestion de la facturation et paiements")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<Page<InvoiceDto>> getAllInvoices(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) Invoice.Status status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Integer amountMin,
            @RequestParam(required = false) Integer amountMax,
            @RequestParam(required = false) String search) {
        
        Page<InvoiceDto> invoices;
        
        if (search != null) {
            invoices = invoiceService.searchByInvoiceNumber(search, pageable);
        } else if (patientId != null || status != null || dateFrom != null || 
                   dateTo != null || amountMin != null || amountMax != null) {
            invoices = invoiceService.findWithFilters(patientId, status, dateFrom, dateTo, 
                                                     amountMin, amountMax, pageable);
        } else {
            invoices = invoiceService.getAllInvoices(pageable);
        }
        
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable UUID id) {
        return invoiceService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceDto> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        return invoiceService.getInvoiceByNumber(invoiceNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InvoiceDto> createInvoice(@Valid @RequestBody CreateInvoiceDto createInvoiceDto) {
        try {
            InvoiceDto createdInvoice = invoiceService.createInvoice(createInvoiceDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInvoice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDto> updateInvoice(@PathVariable UUID id, 
                                                   @Valid @RequestBody UpdateInvoiceDto updateInvoiceDto) {
        try {
            InvoiceDto updatedInvoice = invoiceService.updateInvoice(id, updateInvoiceDto);
            return ResponseEntity.ok(updatedInvoice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InvoiceDto> updateInvoiceStatus(@PathVariable UUID id, 
                                                         @RequestParam Invoice.Status status) {
        try {
            InvoiceDto updatedInvoice = invoiceService.updateInvoiceStatus(id, status);
            return ResponseEntity.ok(updatedInvoice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable UUID id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<InvoiceDto>> getInvoicesByPatient(
            @PathVariable UUID patientId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<InvoiceDto> invoices = invoiceService.getInvoicesByPatient(patientId, pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<InvoiceDto>> getInvoicesByAppointment(@PathVariable UUID appointmentId) {
        List<InvoiceDto> invoices = invoiceService.getInvoicesByAppointment(appointmentId);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<InvoiceDto>> getInvoicesByStatus(
            @PathVariable Invoice.Status status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<InvoiceDto> invoices = invoiceService.getInvoicesByStatus(status, pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/overdue")
    public ResponseEntity<Page<InvoiceDto>> getOverdueInvoices(
            @PageableDefault(size = 20, sort = "dueDate") Pageable pageable) {
        
        Page<InvoiceDto> invoices = invoiceService.getOverdueInvoices(pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/today")
    public ResponseEntity<List<InvoiceDto>> getTodaysInvoices() {
        List<InvoiceDto> invoices = invoiceService.getTodaysInvoices();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/due-between")
    public ResponseEntity<List<InvoiceDto>> getInvoicesDueBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<InvoiceDto> invoices = invoiceService.getInvoicesDueBetween(startDate, endDate);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/stats/status")
    public ResponseEntity<Long> countByStatus(@RequestParam Invoice.Status status) {
        long count = invoiceService.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/total-paid")
    public ResponseEntity<Long> getTotalPaidAmount() {
        Long total = invoiceService.getTotalPaidAmount();
        return ResponseEntity.ok(total != null ? total : 0L);
    }

    @GetMapping("/stats/total-paid-period")
    public ResponseEntity<Long> getTotalPaidAmountBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long total = invoiceService.getTotalPaidAmountBetween(startDate, endDate);
        return ResponseEntity.ok(total != null ? total : 0L);
    }

    @GetMapping("/stats/total-outstanding")
    public ResponseEntity<Long> getTotalOutstandingAmount() {
        Long total = invoiceService.getTotalOutstandingAmount();
        return ResponseEntity.ok(total != null ? total : 0L);
    }

    @GetMapping("/stats/average-amount")
    public ResponseEntity<Double> getAverageInvoiceAmount() {
        Double average = invoiceService.getAverageInvoiceAmount();
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

    @GetMapping("/stats/status-distribution")
    public ResponseEntity<List<Object[]>> getInvoiceStatusStatistics() {
        List<Object[]> stats = invoiceService.getInvoiceStatusStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/by-date")
    public ResponseEntity<List<Object[]>> getInvoiceStatisticsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        
        List<Object[]> stats = invoiceService.getInvoiceStatisticsByDate(startDate);
        return ResponseEntity.ok(stats);
    }
}