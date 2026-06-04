package com.stockhub.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empresa {

    @Id
    @Column(length = 20)
    private String nit;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String direccion;

    @Column(length = 50)
    private String telefono;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Producto> productos = new ArrayList<>();
}
