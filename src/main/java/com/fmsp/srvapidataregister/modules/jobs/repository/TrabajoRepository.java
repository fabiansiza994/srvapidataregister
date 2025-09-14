package com.fmsp.srvapidataregister.modules.jobs.repository;

import com.fmsp.srvapidataregister.modules.jobs.entity.Trabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrabajoRepository extends JpaRepository<Trabajo, Long> {
}
