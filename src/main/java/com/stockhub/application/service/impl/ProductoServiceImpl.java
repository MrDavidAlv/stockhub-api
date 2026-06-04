package com.stockhub.application.service.impl;

import com.stockhub.application.dto.PrecioMonedaRequest;
import com.stockhub.application.dto.ProductoRequest;
import com.stockhub.application.dto.ProductoResponse;
import com.stockhub.application.mapper.ProductoMapper;
import com.stockhub.application.service.ProductoService;
import com.stockhub.domain.exception.DuplicateException;
import com.stockhub.domain.exception.NotFoundException;
import com.stockhub.domain.model.Categoria;
import com.stockhub.domain.model.Empresa;
import com.stockhub.domain.model.PrecioMoneda;
import com.stockhub.domain.model.Producto;
import com.stockhub.domain.port.CategoriaRepository;
import com.stockhub.domain.port.EmpresaRepository;
import com.stockhub.domain.port.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> findAll() {
        return productoRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findById(Long id) {
        return productoRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> notFound(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> findByEmpresa(String empresaNit) {
        if (!empresaRepository.existsById(empresaNit)) {
            throw new NotFoundException("Empresa no encontrada: " + empresaNit);
        }
        return productoRepository.findByEmpresaNit(empresaNit).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ProductoResponse create(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.codigo())) {
            throw new DuplicateException("Producto con codigo " + request.codigo() + " ya existe");
        }
        Empresa empresa = loadEmpresa(request.empresaNit());

        Producto producto = Producto.builder()
                .codigo(request.codigo())
                .nombre(request.nombre())
                .caracteristicas(request.caracteristicas())
                .empresa(empresa)
                .categorias(loadCategorias(request.categoriaIds()))
                .build();

        attachPrecios(producto, request.precios());

        return mapper.toResponse(productoRepository.save(producto));
    }

    @Override
    public ProductoResponse update(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> notFound(id));

        if (!producto.getCodigo().equals(request.codigo())
                && productoRepository.existsByCodigo(request.codigo())) {
            throw new DuplicateException("Producto con codigo " + request.codigo() + " ya existe");
        }

        producto.setCodigo(request.codigo());
        producto.setNombre(request.nombre());
        producto.setCaracteristicas(request.caracteristicas());

        if (!producto.getEmpresa().getNit().equals(request.empresaNit())) {
            producto.setEmpresa(loadEmpresa(request.empresaNit()));
        }

        producto.setCategorias(loadCategorias(request.categoriaIds()));

        producto.getPrecios().clear();
        attachPrecios(producto, request.precios());

        return mapper.toResponse(productoRepository.save(producto));
    }

    @Override
    public void delete(Long id) {
        if (!productoRepository.existsById(id)) {
            throw notFound(id);
        }
        productoRepository.deleteById(id);
    }

    private Empresa loadEmpresa(String nit) {
        return empresaRepository.findById(nit)
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada: " + nit));
    }

    private Set<Categoria> loadCategorias(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        List<Categoria> found = categoriaRepository.findAllById(ids);
        if (found.size() != ids.size()) {
            throw new NotFoundException("Una o mas categorias no existen");
        }
        return new HashSet<>(found);
    }

    private void attachPrecios(Producto producto, List<PrecioMonedaRequest> precios) {
        if (precios == null) {
            return;
        }
        for (PrecioMonedaRequest p : precios) {
            PrecioMoneda precio = PrecioMoneda.builder()
                    .producto(producto)
                    .moneda(p.moneda())
                    .precio(p.precio())
                    .build();
            producto.getPrecios().add(precio);
        }
    }

    private static NotFoundException notFound(Long id) {
        return new NotFoundException("Producto no encontrado: " + id);
    }
}
