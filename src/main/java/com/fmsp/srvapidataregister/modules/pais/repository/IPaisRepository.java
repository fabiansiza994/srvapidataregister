package com.fmsp.srvapidataregister.modules.pais.repository;

import com.fmsp.srvapidataregister.modules.pais.entity.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPaisRepository extends JpaRepository<Pais, Long> {
}
