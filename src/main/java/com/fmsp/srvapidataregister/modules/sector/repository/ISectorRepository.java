package com.fmsp.srvapidataregister.modules.sector.repository;

import com.fmsp.srvapidataregister.modules.sector.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISectorRepository extends JpaRepository<Sector, Long> {
}
