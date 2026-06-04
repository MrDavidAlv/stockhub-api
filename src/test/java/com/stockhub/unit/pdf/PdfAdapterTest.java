package com.stockhub.unit.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.stockhub.domain.model.Categoria;
import com.stockhub.domain.model.Empresa;
import com.stockhub.domain.model.Moneda;
import com.stockhub.domain.model.PrecioMoneda;
import com.stockhub.domain.model.Producto;
import com.stockhub.infrastructure.pdf.PdfAdapter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PdfAdapterTest {

    private final PdfAdapter adapter = new PdfAdapter();

    @Test
    void generarInventarioPdf_emptyList_returnsValidPdfWithEmptyMessage() throws IOException {
        byte[] pdf = adapter.generarInventarioPdf(List.of(), null);

        assertThat(pdf).isNotEmpty();
        assertThat(pdf.length).isGreaterThan(500);
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
        assertThat(extractText(pdf)).contains("Inventario de Productos").contains("Sin productos");
    }

    @Test
    void generarInventarioPdf_withProductos_containsCodigosAndPrecios() throws IOException {
        Empresa empresa = empresa("900111222-3", "ACME SAS");
        Categoria cat = categoria("Software");
        Producto producto = Producto.builder()
                .id(1L)
                .codigo("SKU-001")
                .nombre("Licencia Pro")
                .caracteristicas("Anual")
                .empresa(empresa)
                .build();
        producto.setCategorias(new HashSet<>(Set.of(cat)));
        PrecioMoneda precio = PrecioMoneda.builder()
                .moneda(Moneda.COP)
                .precio(new BigDecimal("1500000.00"))
                .producto(producto)
                .build();
        producto.getPrecios().add(precio);

        byte[] pdf = adapter.generarInventarioPdf(List.of(producto), empresa.getNombre());

        String text = extractText(pdf);
        assertThat(text)
                .contains("Inventario de Productos")
                .contains("Empresa: ACME SAS")
                .contains("SKU-001")
                .contains("Licencia Pro")
                .contains("1500000.00")
                .contains("Software")
                .contains("Total: 1");
    }

    private static String extractText(byte[] pdf) throws IOException {
        try (PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));
             PdfDocument doc = new PdfDocument(reader)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                sb.append(PdfTextExtractor.getTextFromPage(doc.getPage(i)));
            }
            return sb.toString();
        }
    }

    private static Empresa empresa(String nit, String nombre) {
        Empresa e = new Empresa();
        e.setNit(nit);
        e.setNombre(nombre);
        return e;
    }

    private static Categoria categoria(String nombre) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        return c;
    }
}
