package com.fmsp.srvapidataregister.modules.paciente.service;

import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

public interface IPacienteService {
    PacienteDTO save(PacienteDTO pacienteDTO);
    PacienteDTO listPatient(Long clientId);
    long countByCliente(Long clienteId);
    Optional<PacienteDTO> findById(Long aLong);
    void deletePaciente(Long pacienteId, String uuid) throws AccessDeniedException;
    List<PacienteDTO> findByCliente(Long clienteId);
}
