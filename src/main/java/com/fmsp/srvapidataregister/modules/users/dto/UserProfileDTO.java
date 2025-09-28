package com.fmsp.srvapidataregister.modules.users.dto;


import com.fmsp.srvapidataregister.modules.companies.dto.EmpresaLiteDTO;
import com.fmsp.srvapidataregister.modules.groups.dto.GrupoLiteDTO;
import com.fmsp.srvapidataregister.modules.roles.dto.RolLiteDTO;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UserProfileDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String usuario;
    private String email;
    private Integer intentosFallidos;
    private boolean bloqueado;

    private RolLiteDTO rol;
    private GrupoLiteDTO grupo;
    private EmpresaLiteDTO empresa;
}
