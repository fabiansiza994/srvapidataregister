package com.fmsp.srvapidataregister.modules.paciente.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class PacienteDetailDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String documento;
    private String email;
    private String telefono;
    private String direccion;
    private String estado;

    // Enriquecidos
    private Long clienteId;
    private String clienteNombre;
    private String clienteApellido; // opcional si lo tienes
    private Long empresaId;         // útil para auditoría
    private Long grupoId;           // útil para auditoría
    private long trabajosAsociados; // conteo rápido
}
