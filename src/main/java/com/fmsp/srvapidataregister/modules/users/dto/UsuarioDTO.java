package com.fmsp.srvapidataregister.modules.users.dto;

import com.fmsp.srvapidataregister.modules.groups.entity.Grupo;
import com.fmsp.srvapidataregister.modules.roles.entity.Rol;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UsuarioDTO {
    private Long id;
    private String username;
    private String password;
    private Grupo grupo;
    private Rol rol;
    private int intentosFallidos = 0;
    private boolean bloqueado = false;
}
