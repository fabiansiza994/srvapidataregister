package com.fmsp.srvapidataregister.modules.codes;

import com.fmsp.srvapidataregister.modules.codes.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {
    List<Code> findByCodeAndUserEmail(String code, String userEmail);
    List<Code> findByUserEmail(String userEmail);
}
