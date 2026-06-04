package com.stockhub.domain.port;

import com.stockhub.domain.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
}
