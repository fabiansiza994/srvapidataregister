package com.fmsp.srvapidataregister.modules.clients.service.impl;

import com.fmsp.srvapidataregister.modules.clients.repository.ClienteRepository;
import com.fmsp.srvapidataregister.modules.clients.service.IClienteService;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ClienteService implements IClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    public ClienteService(ClienteRepository clienteRepository, ModelMapper modelMapper) {
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PacienteDTO createPaciente(PacienteDTO pacienteDTO) {
        // TODO crear paciente...
        return null;
    }


    /*@PreAuthorize("hasRole('ADMIN_EMPRESA') || (hasRole('USER_NORMAL') && @permisoService.mismoGrupo(#clienteId))")
    public ClienteDTO obtenerCliente(Long clienteId) {
        // Lógica para obtener cliente
        return null;
    }

    @PreAuthorize("hasRole('ADMIN_EMPRESA') || (hasRole('USER_NORMAL') && @permisoService.esCreadorCliente(#clienteId))")
    public ClienteDTO modificarCliente(Long clienteId, ClienteDTO request) {
        // Lógica para modificar cliente
        return null;
    }*/

}
