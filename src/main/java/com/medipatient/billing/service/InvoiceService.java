package com.medipatient.billing.service;

import com.medipatient.appointment.model.Appointment;
import com.medipatient.appointment.repository.AppointmentRepository;
import com.medipatient.billing.dto.CreateInvoiceDto;
import com.medipatient.billing.dto.InvoiceDto;
import com.medipatient.billing.dto.UpdateInvoiceDto;
import com.medipatient.billing.mapper.InvoiceMapper;
import com.medipatient.billing.model.Invoice;
import com.medipatient.billing.repository.InvoiceRepository;
import com.medipatient.patient.model.Patient;
import com.medipatient.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceMapper invoiceMapper;

    @Transactional(readOnly = true)
    public Page<InvoiceDto> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable)
                .map(invoiceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDto> findWithFilters(UUID patientId, Invoice.Status status,
                                           LocalDate dateFrom, LocalDate dateTo,
                                           Integer amountMin, Integer amountMax,
                                           Pageable pageable) {
        return invoiceRepository.findWithFilters(patientId, status, dateFrom, dateTo, 
                                                amountMin, amountMax, pageable)
                .map(invoiceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDto> getInvoicesByPatient(UUID patientId, Pageable pageable) {
        return invoiceRepository.findByPatientId(patientId, pageable)
                .map(invoiceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDto> getInvoicesByStatus(Invoice.Status status, Pageable pageable) {
        return invoiceRepository.findByStatus(status, pageable)
                .map(invoiceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDto> getOverdueInvoices(Pageable pageable) {
        return invoiceRepository.findOverdueInvoices(LocalDate.now(), pageable)
                .map(invoiceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDto> searchByInvoiceNumber(String search, Pageable pageable) {
        return invoiceRepository.searchByInvoiceNumber(search, pageable)
                .map(invoiceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<InvoiceDto> getInvoiceById(UUID id) {
        return invoiceRepository.findById(id)
                .map(invoiceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<InvoiceDto> getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .map(invoiceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<InvoiceDto> getInvoicesByAppointment(UUID appointmentId) {
        return invoiceRepository.findByAppointmentId(appointmentId)
                .stream()
                .map(invoiceMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InvoiceDto> getTodaysInvoices() {
        return invoiceRepository.findTodaysInvoices(LocalDate.now())
                .stream()
                .map(invoiceMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InvoiceDto> getInvoicesDueBetween(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.findInvoicesDueBetween(startDate, endDate)
                .stream()
                .map(invoiceMapper::toDto)
                .toList();
    }

    public InvoiceDto createInvoice(CreateInvoiceDto createInvoiceDto) {
        Patient patient = patientRepository.findById(createInvoiceDto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + createInvoiceDto.getPatientId()));

        // Calculer le montant total si les items sont fournis
        Integer calculatedAmount = createInvoiceDto.getItems().stream()
                .mapToInt(item -> {
                    int totalPrice = item.getQuantity() * item.getUnitPrice();
                    item.setTotalPrice(totalPrice);
                    return totalPrice;
                })
                .sum();

        // Vérifier que le montant correspond au total calculé
        if (!calculatedAmount.equals(createInvoiceDto.getAmount())) {
            throw new IllegalArgumentException("Invoice amount does not match calculated total from items");
        }

        // Convertir les DTOs d'items en entités
        List<Invoice.InvoiceItem> items = createInvoiceDto.getItems()
                .stream()
                .map(invoiceMapper::itemToEntity)
                .collect(Collectors.toList());

        Invoice invoice = invoiceMapper.toEntity(createInvoiceDto);
        invoice.setPatient(patient);
        invoice.setItems(items);

        // Si un rendez-vous est associé, le lier
        if (createInvoiceDto.getAppointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(createInvoiceDto.getAppointmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + createInvoiceDto.getAppointmentId()));
            invoice.setAppointment(appointment);
        }

        // Générer le numéro de facture si non fourni (sera géré par le trigger de base de données)
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Created new invoice with id: {} and number: {}", savedInvoice.getId(), savedInvoice.getInvoiceNumber());
        
        return invoiceMapper.toDto(savedInvoice);
    }

    public InvoiceDto updateInvoice(UUID id, UpdateInvoiceDto updateInvoiceDto) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + id));

        // Empêcher la modification des factures payées ou annulées
        if (invoice.getStatus() == Invoice.Status.PAID || invoice.getStatus() == Invoice.Status.CANCELLED) {
            throw new IllegalArgumentException("Cannot modify paid or cancelled invoices");
        }

        // Mettre à jour les items si fournis
        if (updateInvoiceDto.getItems() != null) {
            List<Invoice.InvoiceItem> items = updateInvoiceDto.getItems()
                    .stream()
                    .map(dto -> Invoice.InvoiceItem.builder()
                            .description(dto.getDescription())
                            .quantity(dto.getQuantity())
                            .unitPrice(dto.getUnitPrice())
                            .totalPrice(dto.getQuantity() * dto.getUnitPrice())
                            .build())
                    .collect(Collectors.toList());
            invoice.setItems(items);

            // Recalculer le montant total
            Integer calculatedAmount = items.stream()
                    .mapToInt(Invoice.InvoiceItem::getTotalPrice)
                    .sum();
            invoice.setAmount(calculatedAmount);
        }

        invoiceMapper.updateEntityFromDto(updateInvoiceDto, invoice);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        log.info("Updated invoice with id: {}", savedInvoice.getId());
        
        return invoiceMapper.toDto(savedInvoice);
    }

    public InvoiceDto updateInvoiceStatus(UUID id, Invoice.Status status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + id));

        // Logique métier pour les transitions de statut
        if (status == Invoice.Status.PAID && invoice.getStatus() == Invoice.Status.CANCELLED) {
            throw new IllegalArgumentException("Cannot mark cancelled invoice as paid");
        }

        invoice.setStatus(status);

        // Si marquée comme en retard, vérifier la date d'échéance
        if (status == Invoice.Status.OVERDUE && 
            (invoice.getDueDate() == null || !invoice.getDueDate().isBefore(LocalDate.now()))) {
            throw new IllegalArgumentException("Cannot mark invoice as overdue - due date is not past");
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Updated invoice {} status to {}", id, status);
        
        return invoiceMapper.toDto(savedInvoice);
    }

    public void deleteInvoice(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + id));

        // Empêcher la suppression des factures payées
        if (invoice.getStatus() == Invoice.Status.PAID) {
            throw new IllegalArgumentException("Cannot delete paid invoices");
        }

        invoiceRepository.delete(invoice);
        log.info("Deleted invoice with id: {}", id);
    }

    @Transactional(readOnly = true)
    public long countByStatus(Invoice.Status status) {
        return invoiceRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public Long getTotalPaidAmount() {
        return invoiceRepository.getTotalPaidAmount();
    }

    @Transactional(readOnly = true)
    public Long getTotalPaidAmountBetween(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.getTotalPaidAmountBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Long getTotalOutstandingAmount() {
        return invoiceRepository.getTotalOutstandingAmount();
    }

    @Transactional(readOnly = true)
    public Double getAverageInvoiceAmount() {
        return invoiceRepository.getAverageInvoiceAmount();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getInvoiceStatusStatistics() {
        return invoiceRepository.countByStatusGrouped();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getInvoiceStatisticsByDate(LocalDate startDate) {
        return invoiceRepository.getInvoiceStatisticsByDate(startDate);
    }

    @Transactional(readOnly = true)
    public boolean existsByInvoiceNumber(String invoiceNumber) {
        return invoiceRepository.existsByInvoiceNumber(invoiceNumber);
    }
}