package com.fmsp.srvapidataregister.modules.codes;

import com.fmsp.srvapidataregister.modules.codes.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {
}
