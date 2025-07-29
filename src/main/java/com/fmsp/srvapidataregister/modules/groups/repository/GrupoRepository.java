package com.fmsp.srvapidataregister.modules.groups.repository;

import com.fmsp.srvapidataregister.modules.groups.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
}
