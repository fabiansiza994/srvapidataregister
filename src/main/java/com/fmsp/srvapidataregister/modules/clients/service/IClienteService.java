package com.fmsp.srvapidataregister.modules.clients.service;

import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;

public interface IClienteService {
    PacienteDTO createPaciente(PacienteDTO pacienteDTO);
}
