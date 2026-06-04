package com.stockhub.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "orden_producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenProducto {

    @EmbeddedId
    private OrdenProductoId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("ordenId")
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("productoId")
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 18, scale = 2)
    private BigDecimal precioUnitario;
}
