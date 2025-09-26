package com.fmsp.srvapidataregister.modules.companies.repository;

import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Empresa findByNit(String nit);
    Empresa findByNombre(String nombre);
}
