package com.stockhub.application.service;

import com.stockhub.application.dto.EmpresaRequest;
import com.stockhub.application.dto.EmpresaResponse;

import java.util.List;

public interface EmpresaService {

    List<EmpresaResponse> findAll();

    EmpresaResponse findByNit(String nit);

    EmpresaResponse create(EmpresaRequest request);

    EmpresaResponse update(String nit, EmpresaRequest request);

    void delete(String nit);
}
