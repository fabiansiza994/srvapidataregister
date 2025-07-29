package com.fmsp.srvapidataregister.modules.roles.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class RolDTO {
    private Long id;
    private String nombre; // Ejemplo: ADMIN, USER, CLIENT
    private String descripcion;
}
