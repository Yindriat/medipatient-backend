package com.medipatient.stock.mapper;

import com.medipatient.inventory.mapper.InventoryMapper;
import com.medipatient.profile.mapper.ProfileMapper;
import com.medipatient.stock.dto.CreateStockMovementDto;
import com.medipatient.stock.dto.StockMovementDto;
import com.medipatient.stock.model.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        uses = {InventoryMapper.class, ProfileMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StockMovementMapper {

    StockMovementDto toDto(StockMovement stockMovement);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    StockMovement toEntity(CreateStockMovementDto createStockMovementDto);
}