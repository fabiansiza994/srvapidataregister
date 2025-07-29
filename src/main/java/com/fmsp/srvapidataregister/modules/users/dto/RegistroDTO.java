package com.fmsp.srvapidataregister.modules.users.dto;

import com.fmsp.srvapidataregister.modules.companies.entity.Empresa;
import com.fmsp.srvapidataregister.modules.groups.entity.Grupo;
import com.fmsp.srvapidataregister.modules.roles.dto.RolDTO;
import com.fmsp.srvapidataregister.modules.roles.entity.Rol;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class RegistroDTO {

    private Long id;
    @NotBlank(message = "El usuario es requerido")
    private String usuario;
    @NotBlank(message = "El nombre es requerido")
    private String nombre;
    @NotBlank(message = "El apellido es requerido")
    private String apellido;
    @NotBlank(message = "El email es requerido")
    private String email;
    @NotBlank(message = "La contraseña es requerida")
    private String password;

    @NotNull(message = "La empresa es requerida")
    @Valid
    private Empresa empresa;
    @NotNull(message = "El rol es requerido")
    @Valid
    private RolDTO rol = new RolDTO();
    @NotNull(message = "El grupo es requerido")
    @Valid
    private Grupo grupo = new Grupo();
}