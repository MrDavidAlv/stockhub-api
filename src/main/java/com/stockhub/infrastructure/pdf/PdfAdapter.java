package com.stockhub.infrastructure.pdf;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.stockhub.domain.model.Categoria;
import com.stockhub.domain.model.Moneda;
import com.stockhub.domain.model.PrecioMoneda;
import com.stockhub.domain.model.Producto;
import com.stockhub.domain.port.PdfPort;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PdfAdapter implements PdfPort {

    private static final String[] HEADERS = {"Codigo", "Nombre", "Empresa", "Precio COP", "Categorias"};
    private static final float[] COL_WIDTHS = {2f, 3f, 3f, 2f, 3f};
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public byte[] generarInventarioPdf(List<Producto> productos, String empresaNombre) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            doc.add(new Paragraph("Inventario de Productos")
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER));

            String subtitulo = (empresaNombre != null && !empresaNombre.isBlank())
                    ? "Empresa: " + empresaNombre
                    : "Todas las empresas";
            doc.add(new Paragraph(subtitulo).setFontSize(11));
            doc.add(new Paragraph("Generado: " + LocalDateTime.now().format(TS)).setFontSize(9));

            Table table = new Table(UnitValue.createPercentArray(COL_WIDTHS)).useAllAvailableWidth();
            for (String h : HEADERS) {
                table.addHeaderCell(
                        new Cell().add(new Paragraph(h).setBold())
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            for (Producto p : productos) {
                table.addCell(safe(p.getCodigo()));
                table.addCell(safe(p.getNombre()));
                table.addCell(safe(p.getEmpresa() != null ? p.getEmpresa().getNombre() : ""));
                table.addCell(formatPrecioCop(p.getPrecios()));
                table.addCell(formatCategorias(p.getCategorias()));
            }

            if (productos.isEmpty()) {
                Cell empty = new Cell(1, HEADERS.length)
                        .add(new Paragraph("Sin productos para los criterios indicados")
                                .setItalic()
                                .setTextAlignment(TextAlignment.CENTER));
                table.addCell(empty);
            }

            doc.add(table);
            doc.add(new Paragraph("Total: " + productos.size() + " producto(s)")
                    .setFontSize(9)
                    .setMarginTop(8f));
        } catch (IOException e) {
            throw new PdfGenerationException("Error generando PDF de inventario", e);
        }
        return baos.toByteArray();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String formatPrecioCop(List<PrecioMoneda> precios) {
        if (precios == null || precios.isEmpty()) {
            return "-";
        }
        return precios.stream()
                .filter(pm -> pm.getMoneda() == Moneda.COP)
                .map(PrecioMoneda::getPrecio)
                .findFirst()
                .map(BigDecimal::toPlainString)
                .orElse("-");
    }

    private static String formatCategorias(java.util.Set<Categoria> categorias) {
        if (categorias == null || categorias.isEmpty()) {
            return "-";
        }
        return categorias.stream()
                .map(Categoria::getNombre)
                .sorted()
                .collect(Collectors.joining(", "));
    }
}
