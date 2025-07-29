package com.fmsp.srvapidataregister.modules.groups.dto;

import com.fmsp.srvapidataregister.modules.companies.dto.EmpresaDTO;
import com.fmsp.srvapidataregister.modules.users.dto.UsuarioDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter @Getter
public class GrupoDTO {
    private Long id;
    private String nombre;
    private EmpresaDTO empresa;
    private Set<UsuarioDTO> usuarios;
}
