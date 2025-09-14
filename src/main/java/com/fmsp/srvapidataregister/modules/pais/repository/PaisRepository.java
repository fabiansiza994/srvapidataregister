package com.fmsp.srvapidataregister.modules.pais.repository;

import com.fmsp.srvapidataregister.modules.pais.entity.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {
    List<Pais> findAll();
}
