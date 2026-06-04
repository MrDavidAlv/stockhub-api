package com.stockhub.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
    name = "precios_moneda",
    uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "moneda"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrecioMoneda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Moneda moneda;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal precio;
}
