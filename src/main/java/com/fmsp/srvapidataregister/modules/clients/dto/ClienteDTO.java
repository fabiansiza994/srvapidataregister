package com.fmsp.srvapidataregister.modules.clients.dto;

import com.fmsp.srvapidataregister.modules.companies.dto.EmpresaDTO;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import com.fmsp.srvapidataregister.modules.users.dto.UsuarioDTO;

import java.util.List;

public class ClienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String direccion;
    private String telefono;
    private EmpresaDTO empresa;
    private UsuarioDTO usuario;
    private String estado = "ACTIVO";
    private List<PacienteDTO> pacientes;
}
