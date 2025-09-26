package com.fmsp.srvapidataregister.modules.users.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UsuarioDetailDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String usuario;
    private String email;
    private Long grupoId;
    private String grupoNombre;
    private Long rolId;
    private String rolNombre;
    private int intentosFallidos;
    private boolean bloqueado;
}
