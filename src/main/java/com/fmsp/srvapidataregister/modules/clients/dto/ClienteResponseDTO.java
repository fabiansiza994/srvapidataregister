package com.fmsp.srvapidataregister.modules.clients.dto;

import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClienteResponseDTO {
    private Long id;
    private String identificacion;
    @NotBlank(message = "el nombre es requerido")
    private String nombre;
    @NotBlank(message = "el apellido es requerido")
    private String apellido;
    @NotBlank(message = "el email es requerido")
    private String email;
    @NotBlank(message = "la direccion es requerida")
    private String direccion;
    private String telefono;
    private String estado = "ACTIVO";
    private List<PacienteDTO> pacientes;
}
