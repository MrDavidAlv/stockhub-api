package com.stockhub.domain.port;

import com.stockhub.domain.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigo(String codigo);
    List<Producto> findByEmpresaNit(String empresaNit);
    boolean existsByCodigo(String codigo);
}
