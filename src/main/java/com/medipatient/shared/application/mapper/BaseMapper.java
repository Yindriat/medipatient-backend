package com.medipatient.shared.application.mapper;

import org.mapstruct.MappingTarget;

import java.util.List;

public interface BaseMapper<E, D> {
    
    D toDto(E entity);
    
    E toEntity(D dto);
    
    List<D> toDtoList(List<E> entities);
    
    List<E> toEntityList(List<D> dtos);
    
    void updateEntityFromDto(D dto, @MappingTarget E entity);
}