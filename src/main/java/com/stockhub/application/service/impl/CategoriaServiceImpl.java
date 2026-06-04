package com.stockhub.application.service.impl;

import com.stockhub.application.dto.CategoriaRequest;
import com.stockhub.application.dto.CategoriaResponse;
import com.stockhub.application.mapper.CategoriaMapper;
import com.stockhub.application.service.CategoriaService;
import com.stockhub.domain.exception.DuplicateException;
import com.stockhub.domain.exception.NotFoundException;
import com.stockhub.domain.model.Categoria;
import com.stockhub.domain.port.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> findAll() {
        return categoriaRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse findById(Long id) {
        return categoriaRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> notFound(id));
    }

    @Override
    public CategoriaResponse create(CategoriaRequest request) {
        categoriaRepository.findByNombre(request.nombre()).ifPresent(c -> {
            throw new DuplicateException("Categoria con nombre '" + request.nombre() + "' ya existe");
        });
        Categoria saved = categoriaRepository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    @Override
    public CategoriaResponse update(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> notFound(id));
        categoriaRepository.findByNombre(request.nombre())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new DuplicateException("Categoria con nombre '" + request.nombre() + "' ya existe");
                });
        mapper.update(request, categoria);
        return mapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public void delete(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw notFound(id);
        }
        categoriaRepository.deleteById(id);
    }

    private static NotFoundException notFound(Long id) {
        return new NotFoundException("Categoria no encontrada: " + id);
    }
}
