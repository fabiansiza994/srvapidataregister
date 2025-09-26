package com.fmsp.srvapidataregister.modules.groups.dto;

import com.fmsp.srvapidataregister.modules.users.dto.UsuarioDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class GrupoDetailDTO {
    private Long id;
    private String nombre;
    private Long empresaId;
    private String empresaNombre;
    private List<UsuarioDTO> usuarios;
}
