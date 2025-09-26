package com.fmsp.srvapidataregister.modules.groups.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class GrupoUpdateDTO {
    @NotBlank(message = "El nombre del grupo es obligatorio")
    private String nombre;
}
