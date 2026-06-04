package com.stockhub.interfaces.rest.controller;

import com.stockhub.application.dto.ProductoRequest;
import com.stockhub.application.dto.ProductoResponse;
import com.stockhub.application.service.ProductoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos")
@PreAuthorize("hasRole('ADMIN')")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public List<ProductoResponse> findAll() {
        return productoService.findAll();
    }

    @GetMapping("/{id}")
    public ProductoResponse findById(@PathVariable Long id) {
        return productoService.findById(id);
    }

    @GetMapping("/por-empresa/{nit}")
    public List<ProductoResponse> findByEmpresa(@PathVariable String nit) {
        return productoService.findByEmpresa(nit);
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> create(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.create(request));
    }

    @PutMapping("/{id}")
    public ProductoResponse update(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return productoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
