package com.fmsp.srvapidataregister.modules.companies.dto;

import com.fmsp.srvapidataregister.modules.groups.dto.GrupoDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String nit;
    private String estado; // ACTIVO, INACTIVO, PENDIENTE
    private List<GrupoDTO> grupos;
}
