package com.stockhub.application.service;

import com.stockhub.application.dto.ProductoRequest;
import com.stockhub.application.dto.ProductoResponse;

import java.util.List;

public interface ProductoService {

    List<ProductoResponse> findAll();

    ProductoResponse findById(Long id);

    List<ProductoResponse> findByEmpresa(String empresaNit);

    ProductoResponse create(ProductoRequest request);

    ProductoResponse update(Long id, ProductoRequest request);

    void delete(Long id);
}
