package com.medipatient.billing.mapper;

import com.medipatient.appointment.mapper.AppointmentMapper;
import com.medipatient.billing.dto.CreateInvoiceDto;
import com.medipatient.billing.dto.InvoiceDto;
import com.medipatient.billing.dto.UpdateInvoiceDto;
import com.medipatient.billing.model.Invoice;
import com.medipatient.patient.mapper.PatientMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        uses = {PatientMapper.class, AppointmentMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvoiceMapper {

    @Mapping(target = "items", source = "items")
    InvoiceDto toDto(Invoice invoice);

    InvoiceDto.InvoiceItemDto itemToDto(Invoice.InvoiceItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Invoice toEntity(CreateInvoiceDto createInvoiceDto);

    @Mapping(target = "totalPrice", expression = "java(itemDto.getQuantity() * itemDto.getUnitPrice())")
    Invoice.InvoiceItem itemToEntity(CreateInvoiceDto.InvoiceItemDto itemDto);

    void updateEntityFromDto(UpdateInvoiceDto updateInvoiceDto, @MappingTarget Invoice invoice);

    @Mapping(target = "totalPrice", expression = "java(itemDto.getQuantity() != null && itemDto.getUnitPrice() != null ? itemDto.getQuantity() * itemDto.getUnitPrice() : item.getTotalPrice())")
    void updateItemFromDto(UpdateInvoiceDto.InvoiceItemDto itemDto, @MappingTarget Invoice.InvoiceItem item);
}