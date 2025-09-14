package com.fmsp.srvapidataregister.modules.clients.service;

import com.fmsp.srvapidataregister.modules.clients.dto.ClienteDTO;
import com.fmsp.srvapidataregister.modules.clients.dto.ClientePlanoDTO;
import com.fmsp.srvapidataregister.modules.clients.dto.ClienteResponseDTO;

import java.util.List;

public interface IClienteService {
    ClienteResponseDTO createClient(ClienteDTO clienteDTO, String uuid);
    List<ClientePlanoDTO> listarClientes();
}
