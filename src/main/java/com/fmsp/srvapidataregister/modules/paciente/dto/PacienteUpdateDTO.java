package com.fmsp.srvapidataregister.modules.paciente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class PacienteUpdateDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no debe exceder 120 caracteres")
    private String nombre;

    @Size(max = 120, message = "El apellido no debe exceder 120 caracteres")
    private String apellido;

    @Size(max = 30, message = "El documento no debe exceder 30 caracteres")
    private String documento;

    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 150, message = "El correo no debe exceder 150 caracteres")
    private String email;

    @Size(max = 30, message = "El teléfono no debe exceder 30 caracteres")
    private String telefono;

    @Size(max = 200, message = "La dirección no debe exceder 200 caracteres")
    private String direccion;

    @Size(max = 20, message = "El estado no debe exceder 20 caracteres")
    private String estado;
}
