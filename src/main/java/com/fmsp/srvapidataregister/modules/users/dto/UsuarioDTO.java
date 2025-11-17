package com.fmsp.srvapidataregister.modules.users.dto;

import com.fmsp.srvapidataregister.modules.groups.dto.GrupoDTO;
import com.fmsp.srvapidataregister.modules.roles.dto.RolDTO;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UsuarioDTO {
    private Long id;
    private String usuario;
    private String nombre;
    private String apellido;
    private String password;
    private String email;
    private GrupoDTO grupo;
    private RolDTO rol;
    private Boolean recoveryStatus;
    private int intentosFallidos;
    private boolean bloqueado = false;
}