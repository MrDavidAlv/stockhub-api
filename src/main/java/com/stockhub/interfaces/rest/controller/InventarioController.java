package com.stockhub.interfaces.rest.controller;

import com.stockhub.application.dto.EmailInventarioRequest;
import com.stockhub.application.dto.ProductoResponse;
import com.stockhub.application.service.InventarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public List<ProductoResponse> getInventario(@RequestParam(required = false) String empresaNit) {
        return inventarioService.getInventario(empresaNit);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf(@RequestParam(required = false) String empresaNit) {
        byte[] pdf = inventarioService.generarPdf(empresaNit);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventario.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/enviar-email")
    public ResponseEntity<Void> enviarPorEmail(@Valid @RequestBody EmailInventarioRequest request) {
        inventarioService.enviarPorEmail(request);
        return ResponseEntity.noContent().build();
    }
}
