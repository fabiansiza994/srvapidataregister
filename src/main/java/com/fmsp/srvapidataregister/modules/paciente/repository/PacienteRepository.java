package com.fmsp.srvapidataregister.modules.paciente.repository;

import com.fmsp.srvapidataregister.modules.paciente.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Paciente findByCliente_id(Long id);
}
