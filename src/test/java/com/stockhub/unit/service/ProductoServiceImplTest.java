package com.stockhub.unit.service;

import com.stockhub.application.dto.PrecioMonedaRequest;
import com.stockhub.application.dto.ProductoRequest;
import com.stockhub.application.dto.ProductoResponse;
import com.stockhub.application.mapper.ProductoMapper;
import com.stockhub.application.service.impl.ProductoServiceImpl;
import com.stockhub.domain.exception.DuplicateException;
import com.stockhub.domain.exception.NotFoundException;
import com.stockhub.domain.model.Categoria;
import com.stockhub.domain.model.Empresa;
import com.stockhub.domain.model.Moneda;
import com.stockhub.domain.model.Producto;
import com.stockhub.domain.port.CategoriaRepository;
import com.stockhub.domain.port.EmpresaRepository;
import com.stockhub.domain.port.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private EmpresaRepository empresaRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private ProductoMapper mapper;

    @InjectMocks private ProductoServiceImpl service;

    private static final String NIT = "900111222-3";
    private static final String CODIGO = "SKU-001";

    @Test
    void create_persistsProductoWithPreciosAndCategorias() {
        ProductoRequest req = new ProductoRequest(
                CODIGO, "Producto X", "spec", NIT, Set.of(1L, 2L),
                List.of(new PrecioMonedaRequest(Moneda.COP, new BigDecimal("100.00")))
        );
        Empresa empresa = empresaWithNit(NIT);
        Categoria c1 = categoriaWithId(1L);
        Categoria c2 = categoriaWithId(2L);

        when(productoRepository.existsByCodigo(CODIGO)).thenReturn(false);
        when(empresaRepository.findById(NIT)).thenReturn(Optional.of(empresa));
        when(categoriaRepository.findAllById(Set.of(1L, 2L))).thenReturn(List.of(c1, c2));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Producto.class))).thenReturn(stubResponse());

        ProductoResponse result = service.create(req);

        assertThat(result).isNotNull();
        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        Producto saved = captor.getValue();
        assertThat(saved.getCodigo()).isEqualTo(CODIGO);
        assertThat(saved.getEmpresa()).isSameAs(empresa);
        assertThat(saved.getCategorias()).hasSize(2);
        assertThat(saved.getPrecios()).hasSize(1);
        assertThat(saved.getPrecios().get(0).getProducto()).isSameAs(saved);
    }

    @Test
    void create_throwsDuplicate_whenCodigoExists() {
        ProductoRequest req = new ProductoRequest(CODIGO, "X", null, NIT, null, null);
        when(productoRepository.existsByCodigo(CODIGO)).thenReturn(true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(DuplicateException.class)
                .hasMessageContaining(CODIGO);
        verify(productoRepository, never()).save(any());
    }

    @Test
    void create_throwsNotFound_whenEmpresaMissing() {
        ProductoRequest req = new ProductoRequest(CODIGO, "X", null, NIT, null, null);
        when(productoRepository.existsByCodigo(CODIGO)).thenReturn(false);
        when(empresaRepository.findById(NIT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NIT);
    }

    @Test
    void create_throwsNotFound_whenCategoriaMissing() {
        ProductoRequest req = new ProductoRequest(CODIGO, "X", null, NIT, Set.of(1L, 99L), null);
        when(productoRepository.existsByCodigo(CODIGO)).thenReturn(false);
        when(empresaRepository.findById(NIT)).thenReturn(Optional.of(empresaWithNit(NIT)));
        when(categoriaRepository.findAllById(Set.of(1L, 99L)))
                .thenReturn(List.of(categoriaWithId(1L)));

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("categorias");
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(productoRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(NotFoundException.class);
        verify(productoRepository, never()).deleteById(any());
    }

    @Test
    void findByEmpresa_throwsNotFound_whenEmpresaMissing() {
        when(empresaRepository.existsById(NIT)).thenReturn(false);

        assertThatThrownBy(() -> service.findByEmpresa(NIT))
                .isInstanceOf(NotFoundException.class);
    }

    private static Empresa empresaWithNit(String nit) {
        Empresa e = new Empresa();
        e.setNit(nit);
        e.setNombre("Empresa " + nit);
        return e;
    }

    private static Categoria categoriaWithId(Long id) {
        Categoria c = new Categoria();
        c.setId(id);
        c.setNombre("Cat " + id);
        return c;
    }

    private static ProductoResponse stubResponse() {
        return new ProductoResponse(1L, CODIGO, "Producto X", "spec", NIT, "X", Set.of(), List.of(), null);
    }
}
