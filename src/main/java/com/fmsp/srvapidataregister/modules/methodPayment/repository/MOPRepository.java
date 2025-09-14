package com.fmsp.srvapidataregister.modules.methodPayment.repository;

import com.fmsp.srvapidataregister.modules.methodPayment.entity.FormaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MOPRepository extends JpaRepository<FormaPago, Long> {
}
