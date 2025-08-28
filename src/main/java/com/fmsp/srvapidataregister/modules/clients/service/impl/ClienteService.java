package com.fmsp.srvapidataregister.modules.clients.service.impl;

import com.fmsp.srvapidataregister.core.exceptions.CustomServiceException;
import com.fmsp.srvapidataregister.modules.clients.dto.ClienteDTO;
import com.fmsp.srvapidataregister.modules.clients.dto.ClienteResponseDTO;
import com.fmsp.srvapidataregister.modules.clients.entity.Cliente;
import com.fmsp.srvapidataregister.modules.clients.repository.ClienteRepository;
import com.fmsp.srvapidataregister.modules.clients.service.IClienteService;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import com.fmsp.srvapidataregister.modules.paciente.service.IPacienteService;
import com.fmsp.srvapidataregister.modules.users.service.IUsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService implements IClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    private final IUsuarioService usuarioService;
    private final IPacienteService pacienteService;

    public ClienteService(ClienteRepository clienteRepository, ModelMapper modelMapper, IUsuarioService usuarioService, IPacienteService pacienteService) {
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
        this.usuarioService = usuarioService;
        this.pacienteService = pacienteService;
    }

    @Override
    public ClienteResponseDTO createClient(ClienteDTO clienteDTO, String uuid) {
        var usuario = usuarioService.getUsuarioById(clienteDTO.getUsuario().getId());
        if(usuario.isEmpty()){
            throw new CustomServiceException(uuid, "E003", "Usuario no encontrado");
        }
        if(usuario.get().getGrupo().getEmpresa().getSector().getNombre().equalsIgnoreCase("SALUD")){
            if(!clienteDTO.getPacientes().isEmpty()){
                var pacienteList = new ArrayList<PacienteDTO>();
                createPatientList(clienteDTO, pacienteList);
                guardarPaciente(pacienteList);
                clienteDTO.setPacientes(pacienteList);
            }
        }
        var client = modelMapper.map(clienteDTO, Cliente.class);
        var clienteDB = clienteRepository.save(client);

        clienteDTO.setId(clienteDB.getId());
        return modelMapper.map(clienteDTO, ClienteResponseDTO.class);
    }

    private void createPatientList(ClienteDTO clienteDTO, ArrayList<PacienteDTO> pacienteList) {
        clienteDTO.getPacientes().forEach(pacienteDTO -> {
            var paciente = new PacienteDTO();
            paciente.setId(pacienteDTO.getId());
            paciente.setNombre(pacienteDTO.getNombre());
            paciente.setEmail(pacienteDTO.getEmail());
            paciente.setTelefono(pacienteDTO.getTelefono());
            paciente.setDireccion(pacienteDTO.getDireccion());
            pacienteList.add(paciente);
        });
    }


    private void guardarPaciente(ArrayList<PacienteDTO> pacienteList) {
        pacienteList.forEach(pacienteService::save);
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
