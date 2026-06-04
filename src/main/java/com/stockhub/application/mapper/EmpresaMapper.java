package com.stockhub.application.mapper;

import com.stockhub.application.dto.EmpresaRequest;
import com.stockhub.application.dto.EmpresaResponse;
import com.stockhub.domain.model.Empresa;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    EmpresaResponse toResponse(Empresa empresa);

    Empresa toEntity(EmpresaRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(EmpresaRequest request, @MappingTarget Empresa empresa);
}
