package com.medipatient.inventory.mapper;

import com.medipatient.inventory.dto.CreateInventoryDto;
import com.medipatient.inventory.dto.InventoryDto;
import com.medipatient.inventory.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventoryMapper {

    @Mapping(target = "isLowStock", expression = "java(inventory.isLowStock())")
    @Mapping(target = "isExpired", expression = "java(inventory.isExpired())")
    @Mapping(target = "isExpiringSoon", expression = "java(inventory.isExpiringSoon(30))")
    InventoryDto toDto(Inventory inventory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", expression = "java(createInventoryDto.getCategory() != null ? createInventoryDto.getCategory() : com.medipatient.inventory.model.Inventory.Category.MEDICATION)")
    @Mapping(target = "currentStock", expression = "java(createInventoryDto.getCurrentStock() != null ? createInventoryDto.getCurrentStock() : 0)")
    @Mapping(target = "minStock", expression = "java(createInventoryDto.getMinStock() != null ? createInventoryDto.getMinStock() : 0)")
    @Mapping(target = "unitPrice", expression = "java(createInventoryDto.getUnitPrice() != null ? createInventoryDto.getUnitPrice() : 0)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Inventory toEntity(CreateInventoryDto createInventoryDto);

    void updateEntityFromDto(CreateInventoryDto updateInventoryDto, @MappingTarget Inventory inventory);
}