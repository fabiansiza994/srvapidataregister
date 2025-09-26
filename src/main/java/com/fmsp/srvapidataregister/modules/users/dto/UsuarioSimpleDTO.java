package com.fmsp.srvapidataregister.modules.users.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UsuarioSimpleDTO {
    private Long id;
    private String usuario;
    private String nombre;
    private String apellido;
    private String email;
    private int intentosFallidos = 0;
    private boolean bloqueado = false;
}