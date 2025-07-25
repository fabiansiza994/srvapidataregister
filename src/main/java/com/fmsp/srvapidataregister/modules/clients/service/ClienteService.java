package com.fmsp.srvapidataregister.modules.clients.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {


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
