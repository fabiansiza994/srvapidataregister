package com.fmsp.srvapidataregister.modules.clients.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ClienteUpdateDTO {
    @NotBlank(message = "La identificación es obligatoria")
    private String identificacion;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "El email no es válido")
    private String email;

    private String direccion;

    private String telefono;
    private String razonSocial;
    private String camaraComercio;
    private String rut;
    private String estado;
}
