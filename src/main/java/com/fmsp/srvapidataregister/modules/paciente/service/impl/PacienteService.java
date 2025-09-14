package com.fmsp.srvapidataregister.modules.paciente.service.impl;

import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import com.fmsp.srvapidataregister.modules.paciente.entity.Paciente;
import com.fmsp.srvapidataregister.modules.paciente.repository.PacienteRepository;
import com.fmsp.srvapidataregister.modules.paciente.service.IPacienteService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class PacienteService implements IPacienteService {

    private final PacienteRepository pacienteRepository;
    private final ModelMapper modelMapper;

    public PacienteService(PacienteRepository pacienteRepository, ModelMapper modelMapper) {
        this.pacienteRepository = pacienteRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PacienteDTO save(PacienteDTO pacienteDTO) {
        var paciente = modelMapper.map(pacienteDTO, Paciente.class);
        paciente = pacienteRepository.save(paciente);
        return modelMapper.map(paciente, PacienteDTO.class);
    }

    @Override
    public PacienteDTO listPatient(Long clientId) {
        var paciente = pacienteRepository.findByCliente_id(clientId);
        paciente = modelMapper.map(paciente, Paciente.class);
        return modelMapper.map(paciente, PacienteDTO.class);
    }
}
