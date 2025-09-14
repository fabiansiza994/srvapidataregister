package com.fmsp.srvapidataregister.modules.paciente.service.impl;

import com.fmsp.srvapidataregister.modules.auth.service.PermisoService;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import com.fmsp.srvapidataregister.modules.paciente.entity.Paciente;
import com.fmsp.srvapidataregister.modules.paciente.repository.PacienteRepository;
import com.fmsp.srvapidataregister.modules.paciente.service.IPacienteService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PacienteService implements IPacienteService {

    private PermisoService permisoService;
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

        var pacienteDB = modelMapper.map(paciente, PacienteDTO.class);
        if(pacienteDTO.getClienteId() != null) {
            pacienteDB.setClienteId(pacienteDTO.getClienteId());
        }
        return modelMapper.map(pacienteDB, PacienteDTO.class);
    }

    @Override
    public PacienteDTO listPatient(Long clientId) {
        var paciente = pacienteRepository.findByCliente(clientId);
        paciente = modelMapper.map(paciente, Paciente.class);
        return modelMapper.map(paciente, PacienteDTO.class);
    }

    @Override
    public Optional<PacienteDTO> findById(Long id) {
        var paciente = pacienteRepository.findById(id);
        return paciente.map(p -> modelMapper.map(p, PacienteDTO.class));
    }
}
