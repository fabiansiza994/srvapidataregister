package com.fmsp.srvapidataregister.modules.clients.service;

import com.fmsp.srvapidataregister.modules.clients.dto.ClienteDTO;
import com.fmsp.srvapidataregister.modules.clients.dto.ClientePlanoDTO;
import com.fmsp.srvapidataregister.modules.clients.dto.ClienteResponseDTO;
import com.fmsp.srvapidataregister.modules.clients.dto.ClienteUpdateDTO;
import com.fmsp.srvapidataregister.modules.clients.entity.Cliente;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IClienteService {
    ClienteResponseDTO createClient(ClienteDTO clienteDTO, String uuid);

    Page<ClientePlanoDTO> listarClientes(int page, int size, String sortBy, String direction);

    Optional<ClientePlanoDTO> findById(Long id);
    Cliente findClienteById(Long id);

    Page<ClientePlanoDTO> searchClientes(String q, int page, int size, String sortBy, String direction);
    ClienteResponseDTO updateCliente(Long clienteId, ClienteUpdateDTO dto, String uuid);
}
