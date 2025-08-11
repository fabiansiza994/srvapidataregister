package com.fmsp.srvapidataregister.modules.clients.repository;

import com.fmsp.srvapidataregister.modules.clients.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
