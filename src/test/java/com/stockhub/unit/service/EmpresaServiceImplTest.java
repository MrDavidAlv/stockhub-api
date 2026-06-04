package com.stockhub.unit.service;

import com.stockhub.application.dto.EmpresaRequest;
import com.stockhub.application.dto.EmpresaResponse;
import com.stockhub.application.mapper.EmpresaMapper;
import com.stockhub.application.service.impl.EmpresaServiceImpl;
import com.stockhub.domain.exception.DuplicateException;
import com.stockhub.domain.exception.NotFoundException;
import com.stockhub.domain.model.Empresa;
import com.stockhub.domain.port.EmpresaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceImplTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EmpresaMapper mapper;

    @InjectMocks
    private EmpresaServiceImpl service;

    private static final String NIT = "900111222-3";

    @Test
    void findAll_returnsMappedList() {
        Empresa e = new Empresa();
        EmpresaResponse r = new EmpresaResponse(NIT, "X", "y", "z", null);
        when(empresaRepository.findAll()).thenReturn(List.of(e));
        when(mapper.toResponse(e)).thenReturn(r);

        List<EmpresaResponse> result = service.findAll();

        assertThat(result).containsExactly(r);
    }

    @Test
    void findByNit_returnsResponse_whenFound() {
        Empresa e = new Empresa();
        EmpresaResponse r = new EmpresaResponse(NIT, "X", "y", "z", null);
        when(empresaRepository.findById(NIT)).thenReturn(Optional.of(e));
        when(mapper.toResponse(e)).thenReturn(r);

        assertThat(service.findByNit(NIT)).isEqualTo(r);
    }

    @Test
    void findByNit_throwsNotFound_whenMissing() {
        when(empresaRepository.findById(NIT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByNit(NIT))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NIT);
    }

    @Test
    void create_persistsAndReturnsResponse() {
        EmpresaRequest req = new EmpresaRequest(NIT, "TestCo", "Cra 1", "3001234567");
        Empresa entity = new Empresa();
        EmpresaResponse resp = new EmpresaResponse(NIT, "TestCo", "Cra 1", "3001234567", null);

        when(empresaRepository.existsById(NIT)).thenReturn(false);
        when(mapper.toEntity(req)).thenReturn(entity);
        when(empresaRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(resp);

        assertThat(service.create(req)).isEqualTo(resp);
        verify(empresaRepository).save(entity);
    }

    @Test
    void create_throwsDuplicate_whenNitExists() {
        EmpresaRequest req = new EmpresaRequest(NIT, "X", null, null);
        when(empresaRepository.existsById(NIT)).thenReturn(true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(DuplicateException.class)
                .hasMessageContaining(NIT);
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void update_appliesChanges_whenSameNit() {
        EmpresaRequest req = new EmpresaRequest(NIT, "Renamed", "addr", "tel");
        Empresa entity = new Empresa();
        EmpresaResponse resp = new EmpresaResponse(NIT, "Renamed", "addr", "tel", null);

        when(empresaRepository.findById(NIT)).thenReturn(Optional.of(entity));
        when(empresaRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(resp);

        assertThat(service.update(NIT, req)).isEqualTo(resp);
        verify(mapper).update(req, entity);
    }

    @Test
    void update_throwsDuplicate_whenNitChanged() {
        EmpresaRequest req = new EmpresaRequest("OTHER", "X", null, null);
        when(empresaRepository.findById(NIT)).thenReturn(Optional.of(new Empresa()));

        assertThatThrownBy(() -> service.update(NIT, req))
                .isInstanceOf(DuplicateException.class);
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void update_throwsNotFound_whenMissing() {
        EmpresaRequest req = new EmpresaRequest(NIT, "X", null, null);
        when(empresaRepository.findById(NIT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(NIT, req))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_succeeds_whenExists() {
        when(empresaRepository.existsById(NIT)).thenReturn(true);

        service.delete(NIT);

        verify(empresaRepository).deleteById(NIT);
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(empresaRepository.existsById(NIT)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(NIT))
                .isInstanceOf(NotFoundException.class);
        verify(empresaRepository, never()).deleteById(anyString());
    }
}
