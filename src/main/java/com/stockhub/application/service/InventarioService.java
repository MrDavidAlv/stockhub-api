package com.stockhub.application.service;

import com.stockhub.application.dto.EmailInventarioRequest;
import com.stockhub.application.dto.ProductoResponse;

import java.util.List;

public interface InventarioService {

    List<ProductoResponse> getInventario(String empresaNit);

    byte[] generarPdf(String empresaNit);

    void enviarPorEmail(EmailInventarioRequest request);
}
