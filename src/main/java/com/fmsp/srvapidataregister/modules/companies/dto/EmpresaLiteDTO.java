package com.fmsp.srvapidataregister.modules.companies.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class EmpresaLiteDTO {
    private Long id;
    private String nombre;
    private String sectorNombre; // opcional si está en el grafo
    private String paisNombre;
}
