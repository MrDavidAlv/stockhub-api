package com.stockhub.domain.port;

import com.stockhub.domain.model.Producto;

import java.util.List;

public interface PdfPort {

    byte[] generarInventarioPdf(List<Producto> productos, String empresaNombre);
}
