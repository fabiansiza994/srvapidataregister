package com.fmsp.srvapidataregister.modules.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UsuarioUpdateDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "El email no es válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    // Solo ADMIN puede cambiarlos; si no vienen, no se tocan:
    private Long grupoId;
    private Long rolId;

    private Boolean bloqueado;       // opcional
    private Integer intentosFallidos; // opcional
}
