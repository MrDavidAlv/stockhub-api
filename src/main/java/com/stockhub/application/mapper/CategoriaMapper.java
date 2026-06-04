package com.stockhub.application.mapper;

import com.stockhub.application.dto.CategoriaRequest;
import com.stockhub.application.dto.CategoriaResponse;
import com.stockhub.domain.model.Categoria;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoriaResponse toResponse(Categoria categoria);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productos", ignore = true)
    Categoria toEntity(CategoriaRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productos", ignore = true)
    void update(CategoriaRequest request, @MappingTarget Categoria categoria);
}
