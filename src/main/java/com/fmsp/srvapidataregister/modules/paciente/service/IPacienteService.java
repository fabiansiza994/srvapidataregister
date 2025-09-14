package com.fmsp.srvapidataregister.modules.paciente.service;

import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface IPacienteService {
    PacienteDTO save(PacienteDTO pacienteDTO);
    PacienteDTO listPatient(Long clientId);

    Optional<PacienteDTO> findById(Long aLong);
}
