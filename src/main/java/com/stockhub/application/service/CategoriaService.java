package com.stockhub.application.service;

import com.stockhub.application.dto.CategoriaRequest;
import com.stockhub.application.dto.CategoriaResponse;

import java.util.List;

public interface CategoriaService {

    List<CategoriaResponse> findAll();

    CategoriaResponse findById(Long id);

    CategoriaResponse create(CategoriaRequest request);

    CategoriaResponse update(Long id, CategoriaRequest request);

    void delete(Long id);
}
