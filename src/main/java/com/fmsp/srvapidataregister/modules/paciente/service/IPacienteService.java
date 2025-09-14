package com.fmsp.srvapidataregister.modules.paciente.service;

import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;

public interface IPacienteService {
    PacienteDTO save(PacienteDTO pacienteDTO);
    PacienteDTO listPatient(Long clientId);
}
