package com.fmsp.srvapidataregister.modules.groups.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class GrupoListDTO {
    private Long id;
    private String nombre;
    private Long empresaId;
    private String empresaNombre;
    private Integer usuariosCount;
}
