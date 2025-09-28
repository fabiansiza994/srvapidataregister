package com.fmsp.srvapidataregister.modules.paciente.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class PacienteDTO {
    private Long id;
    @NotNull
    private String nombre;
    @NotNull
    private String apellido;
    @NotNull
    private String documento;
    @NotNull
    private String telefono;
    @NotNull
    private String email;
    @NotNull
    private Long clienteId;
    private String clienteNombre;
    private String direccion;
    private String estado = "ACTIVO";
}