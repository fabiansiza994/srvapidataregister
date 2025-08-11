package com.fmsp.srvapidataregister.modules.paciente.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class PacienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String documento; // DNI, cédula, etc.
    private String telefono;
    private String email;
    private String direccion;
    private String estado = "ACTIVO";
}