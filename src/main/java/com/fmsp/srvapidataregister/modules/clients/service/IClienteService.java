package com.fmsp.srvapidataregister.modules.clients.service;

import com.fmsp.srvapidataregister.modules.clients.dto.ClienteDTO;
import com.fmsp.srvapidataregister.modules.clients.dto.ClienteResponseDTO;

public interface IClienteService {
    ClienteResponseDTO createClient(ClienteDTO clienteDTO, String uuid);
}
