package com.stockhub.application.mapper;

import com.stockhub.application.dto.PrecioMonedaResponse;
import com.stockhub.application.dto.ProductoResponse;
import com.stockhub.domain.model.PrecioMoneda;
import com.stockhub.domain.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoriaMapper.class})
public interface ProductoMapper {

    @Mapping(target = "empresaNit", source = "empresa.nit")
    @Mapping(target = "empresaNombre", source = "empresa.nombre")
    ProductoResponse toResponse(Producto producto);

    PrecioMonedaResponse toPrecioResponse(PrecioMoneda precio);
}
