package com.fmsp.srvapidataregister.modules.map.repository;

import com.fmsp.srvapidataregister.modules.map.entity.UsagePing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsagePingRepository extends JpaRepository<UsagePing, Long> {
}
