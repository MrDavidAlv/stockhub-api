package com.stockhub.application.service.impl;

import com.stockhub.application.dto.EmailInventarioRequest;
import com.stockhub.application.dto.ProductoResponse;
import com.stockhub.application.mapper.ProductoMapper;
import com.stockhub.application.service.InventarioService;
import com.stockhub.domain.exception.NotFoundException;
import com.stockhub.domain.model.Empresa;
import com.stockhub.domain.model.Producto;
import com.stockhub.domain.port.EmpresaRepository;
import com.stockhub.domain.port.MailPort;
import com.stockhub.domain.port.PdfPort;
import com.stockhub.domain.port.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventarioServiceImpl implements InventarioService {

    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;
    private final ProductoMapper productoMapper;
    private final PdfPort pdfPort;
    private final MailPort mailPort;

    @Override
    public List<ProductoResponse> getInventario(String empresaNit) {
        return loadProductos(empresaNit).stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    @Override
    public byte[] generarPdf(String empresaNit) {
        List<Producto> productos = loadProductos(empresaNit);
        String empresaNombre = (empresaNit != null && !empresaNit.isBlank())
                ? loadEmpresa(empresaNit).getNombre()
                : null;
        return pdfPort.generarInventarioPdf(productos, empresaNombre);
    }

    @Override
    public void enviarPorEmail(EmailInventarioRequest request) {
        byte[] pdf = generarPdf(request.empresaNit());
        String subject = (request.empresaNit() != null && !request.empresaNit().isBlank())
                ? "Inventario StockHub - " + request.empresaNit()
                : "Inventario StockHub - Todas las empresas";
        String body = "Adjunto el inventario solicitado. Saludos, equipo StockHub.";
        mailPort.enviarPdfAdjunto(request.email(), subject, body, pdf, "inventario.pdf");
    }

    private List<Producto> loadProductos(String empresaNit) {
        if (empresaNit == null || empresaNit.isBlank()) {
            return productoRepository.findAll();
        }
        if (!empresaRepository.existsById(empresaNit)) {
            throw new NotFoundException("Empresa no encontrada: " + empresaNit);
        }
        return productoRepository.findByEmpresaNit(empresaNit);
    }

    private Empresa loadEmpresa(String nit) {
        return empresaRepository.findById(nit)
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada: " + nit));
    }
}
