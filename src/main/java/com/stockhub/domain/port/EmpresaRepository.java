package com.stockhub.domain.port;

import com.stockhub.domain.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, String> {
}
