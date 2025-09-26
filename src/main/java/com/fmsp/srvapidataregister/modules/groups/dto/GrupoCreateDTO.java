package com.fmsp.srvapidataregister.modules.groups.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class GrupoCreateDTO {
    @NotBlank(message = "El nombre del grupo es obligatorio")
    private String nombre;

    @NotNull(message = "La empresa es obligatoria")
    private Long empresaId;
}
