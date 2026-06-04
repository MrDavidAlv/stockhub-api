package com.stockhub.application.service.impl;

import com.stockhub.application.dto.EmpresaRequest;
import com.stockhub.application.dto.EmpresaResponse;
import com.stockhub.application.mapper.EmpresaMapper;
import com.stockhub.application.service.EmpresaService;
import com.stockhub.domain.exception.DuplicateException;
import com.stockhub.domain.exception.NotFoundException;
import com.stockhub.domain.model.Empresa;
import com.stockhub.domain.port.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaResponse> findAll() {
        return empresaRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaResponse findByNit(String nit) {
        return empresaRepository.findById(nit)
                .map(mapper::toResponse)
                .orElseThrow(() -> notFound(nit));
    }

    @Override
    public EmpresaResponse create(EmpresaRequest request) {
        if (empresaRepository.existsById(request.nit())) {
            throw new DuplicateException("Empresa con NIT " + request.nit() + " ya existe");
        }
        Empresa saved = empresaRepository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    @Override
    public EmpresaResponse update(String nit, EmpresaRequest request) {
        Empresa empresa = empresaRepository.findById(nit)
                .orElseThrow(() -> notFound(nit));
        if (!nit.equals(request.nit())) {
            throw new DuplicateException("No se permite cambiar el NIT en una actualizacion");
        }
        mapper.update(request, empresa);
        return mapper.toResponse(empresaRepository.save(empresa));
    }

    @Override
    public void delete(String nit) {
        if (!empresaRepository.existsById(nit)) {
            throw notFound(nit);
        }
        empresaRepository.deleteById(nit);
    }

    private static NotFoundException notFound(String nit) {
        return new NotFoundException("Empresa no encontrada: " + nit);
    }
}
