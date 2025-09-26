package com.fmsp.srvapidataregister.modules.users.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UsuarioListDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String usuario;
    private String email;
    private Long grupoId;
    private String grupoNombre;
    private String rolNombre;
    private boolean bloqueado;
}
